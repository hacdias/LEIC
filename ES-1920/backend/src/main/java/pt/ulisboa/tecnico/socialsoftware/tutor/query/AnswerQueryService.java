package pt.ulisboa.tecnico.socialsoftware.tutor.query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.query.domain.AnswerQuery;
import pt.ulisboa.tecnico.socialsoftware.tutor.query.domain.Query;
import pt.ulisboa.tecnico.socialsoftware.tutor.query.dto.AnswerQueryDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.query.repository.AnswerQueryRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.query.repository.QueryRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

@Service
public class AnswerQueryService {

    @Autowired
    private QueryRepository queryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AnswerQueryRepository answerQueryRepository;

    @PersistenceContext
    EntityManager entityManager;

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public AnswerQueryDto findAnswerQueryById(Integer answerQueryId) {
        return answerQueryRepository.findById(answerQueryId).map(AnswerQueryDto::new)
                .orElseThrow(() -> new TutorException(ANSWER_QUERY_NOT_FOUND, answerQueryId));
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public AnswerQueryDto createAnswerQuery(Integer queryId, Integer teacherId, AnswerQueryDto answerQueryDto) {
        Query query = queryRepository.findById(queryId).orElseThrow(() -> new TutorException(QUERY_NOT_FOUND, queryId));
        User teacher = userRepository.findById(teacherId).orElseThrow(() -> new TutorException(USER_NOT_FOUND, teacherId));

        if (teacher.getRole() != User.Role.TEACHER)
            throw new TutorException(USER_NOT_TEACHER, teacherId);

        checkTeacherCanAnswer(query, teacher);

        AnswerQuery answerQuery = new AnswerQuery(query, teacher, answerQueryDto);
        answerQuery.setCreationDate(LocalDateTime.now());
        this.entityManager.persist(answerQuery);
        return new AnswerQueryDto(answerQuery);
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void removeAnswerQuery(Integer answerQueryId) {
        AnswerQuery answerQuery = answerQueryRepository.findById(answerQueryId).orElseThrow(() -> new TutorException(ANSWER_QUERY_NOT_FOUND, answerQueryId));

        answerQuery.remove();
        entityManager.remove(answerQuery);
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public AnswerQueryDto updateAnswerQuery(Integer answerQueryId, AnswerQueryDto answerQueryDto) {
        AnswerQuery answerQuery = answerQueryRepository.findById(answerQueryId).orElseThrow(() -> new TutorException(ANSWER_QUERY_NOT_FOUND, answerQueryId));

        answerQuery.update(answerQueryDto);
        return new AnswerQueryDto(answerQuery);
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public AnswerQueryDto addFurtherClarification(Integer answerQueryId, Integer userId, AnswerQueryDto furtherClarificationDto) {
        AnswerQuery answerQuery = answerQueryRepository.findById(answerQueryId).orElseThrow(() -> new TutorException(ANSWER_QUERY_NOT_FOUND, answerQueryId));
        User user = userRepository.findById(userId).orElseThrow(() -> new TutorException(USER_NOT_FOUND, userId));

        if (user.getRole() == User.Role.TEACHER) {
            AnswerQuery tmp = answerQuery;
            Query query = tmp.getQuery();

            while (query == null) {
                tmp = tmp.getAnswerQuery();
                query = tmp.getQuery();
            }

            checkTeacherCanAnswer(query, user);
        }

        AnswerQuery furtherClarification = new AnswerQuery(answerQuery, user, furtherClarificationDto);
        furtherClarification.setCreationDate(LocalDateTime.now());
        this.entityManager.persist(furtherClarification);
        return new AnswerQueryDto(furtherClarification);
    }

    private void checkTeacherCanAnswer(Query query, User teacher) {
        Boolean teacherTeaches = false;

        Question question = query.getQuestion();
        Course course = question.getCourse();
        for (CourseExecution courseExecution : teacher.getCourseExecutions()) {
            if (courseExecution.getCourse() == course) teacherTeaches = true;
        }

        if (!teacherTeaches) {
            throw new TutorException(TEACHER_NOT_IN_COURSE);
        }
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<AnswerQueryDto> getAnswersToQuery(Integer queryId) {
        queryRepository.findById(queryId)
                .orElseThrow(() -> new TutorException(QUERY_NOT_FOUND,queryId));

        return answerQueryRepository.findAll()
                .stream()
                .filter(answerQuery -> answerQuery.getQuery() != null && answerQuery.getQuery().getId() == queryId)
                .map(AnswerQueryDto::new)
                .sorted(Comparator.comparing(AnswerQueryDto::getCreationDate))
                .collect(Collectors.toList());
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<AnswerQueryDto> getAnswersByTeacher(Integer teacherId) {
        userRepository.findById(teacherId)
                .orElseThrow(() -> new TutorException(USER_NOT_FOUND,teacherId));

        return answerQueryRepository.findAll()
                .stream()
                .filter(answerQuery -> answerQuery.getUser().getId() == teacherId)
                .map(AnswerQueryDto::new)
                .sorted(Comparator.comparing(AnswerQueryDto::getCreationDate))
                .collect(Collectors.toList());
    }
}
