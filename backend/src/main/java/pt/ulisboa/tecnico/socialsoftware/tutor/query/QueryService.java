package pt.ulisboa.tecnico.socialsoftware.tutor.query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
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

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

@Service
public class QueryService {

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QueryRepository queryRepository;

    @PersistenceContext
    EntityManager entityManager;

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public QueryDto createQuery(Integer questionId, Integer studentId, QueryDto queryDto) {
        Question question = questionRepository.findById(questionId).orElseThrow(() -> new TutorException(QUESTION_NOT_FOUND, questionId));
        User student = userRepository.findById(studentId).orElseThrow(() -> new TutorException(USER_NOT_FOUND, studentId));

        if (student.getRole() != User.Role.STUDENT)
            throw new TutorException(USER_NOT_STUDENT, studentId);

        if (queryDto.getKey() == null) {
            int maxQueryNumber = queryRepository.getMaxQueryNumber() != null ?
                    queryRepository.getMaxQueryNumber() : 0;
            queryDto.setKey(maxQueryNumber + 1);
        }

        Query query = new Query(question, student, queryDto);
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
        if (query.getAnswers().size() != 0)
            throw new TutorException(QUERY_IS_ANSWERED);

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
}
