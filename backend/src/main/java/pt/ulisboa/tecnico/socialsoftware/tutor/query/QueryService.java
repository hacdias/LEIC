package pt.ulisboa.tecnico.socialsoftware.tutor.query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuestionAnswerRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.query.domain.Query;
import pt.ulisboa.tecnico.socialsoftware.tutor.query.dto.QueryDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.query.repository.QueryRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

@Service
public class QueryService {

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QuestionAnswerRepository questionAnswerRepository;

    @Autowired
    private QueryRepository queryRepository;

    @PersistenceContext
    EntityManager entityManager;

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public QueryDto findQueryById(Integer queryId) {
        return queryRepository.findById(queryId).map(QueryDto::new)
                .orElseThrow(() -> new TutorException(QUERY_NOT_FOUND, queryId));
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public QueryDto createQuery(Integer questionId, Integer studentId, Integer questionAnswerId, QueryDto queryDto) {
        Question question = questionRepository.findById(questionId).orElseThrow(() -> new TutorException(QUESTION_NOT_FOUND, questionId));
        User student = userRepository.findById(studentId).orElseThrow(() -> new TutorException(USER_NOT_FOUND, studentId));
        QuestionAnswer questionAnswer = questionAnswerRepository.findById(questionAnswerId).orElseThrow(() -> new TutorException(QUESTION_ANSWER_NOT_FOUND, questionAnswerId));

        if (student.getRole() != User.Role.STUDENT)
            throw new TutorException(USER_NOT_STUDENT, studentId);

        if (questionAnswer.getQuizAnswer().getUser().getId() != student.getId()
                || questionAnswer.getQuizQuestion().getQuestion().getId() != question.getId()) {
            throw new TutorException(QUESTION_NOT_ANSWERED);
        }

        Query query = new Query(question, student, questionAnswer, queryDto);
        query.setCreationDate(LocalDateTime.now());
        this.entityManager.persist(query);
        return new QueryDto(query);
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void removeQuery(Integer queryId) {
        Query query = queryRepository.findById(queryId).orElseThrow(() -> new TutorException(QUERY_NOT_FOUND, queryId));

        query.remove();
        entityManager.remove(query);
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public QueryDto updateQuery(Integer queryId, QueryDto queryDto) {
        Query query = queryRepository.findById(queryId).orElseThrow(() -> new TutorException(QUERY_NOT_FOUND, queryId));
        if (query.getAnswers().size() != 0)
            throw new TutorException(QUERY_IS_ANSWERED);

        query.update(queryDto);
        return new QueryDto(query);
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public QueryDto shareQuery(Integer queryId) {
        return null;
    }


    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<QueryDto> getQueriesToQuestion(Integer questionId) {
            questionRepository.findById(questionId)
                    .orElseThrow(() -> new TutorException(QUESTION_NOT_FOUND,questionId));

            return queryRepository.findAll()
                    .stream()
                    .filter(query -> query.getQuestion().getId() == questionId)
                    .map(QueryDto::new)
                    .sorted(Comparator.comparing(QueryDto::getCreationDate))
                    .collect(Collectors.toList());
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<QueryDto> getQueriesByStudent(Integer studentId) {
        userRepository.findById(studentId)
                .orElseThrow(() -> new TutorException(USER_NOT_FOUND,studentId));

        return queryRepository.findAll()
                .stream()
                .filter(query -> query.getStudent().getId() == studentId)
                .map(QueryDto::new)
                .sorted(Comparator.comparing(QueryDto::getCreationDate))
                .collect(Collectors.toList());
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<QueryDto> getQueriesInTeachersCourse(Integer teacherId) {
        User teacher = userRepository.findById(teacherId)
                .orElseThrow(() -> new TutorException(USER_NOT_FOUND,teacherId));

        List<Integer> teacherCourses = teacher.getCourseExecutions().stream()
                .map(courseExecution -> courseExecution.getCourse().getId())
                .collect(Collectors.toList());

        return queryRepository.findAll()
                .stream()
                .filter(query ->  teacherCourses.contains(query.getQuestion().getCourse().getId()))
                .map(QueryDto::new)
                .sorted(Comparator.comparing(QueryDto::getCreationDate))
                .collect(Collectors.toList());
    }
}
