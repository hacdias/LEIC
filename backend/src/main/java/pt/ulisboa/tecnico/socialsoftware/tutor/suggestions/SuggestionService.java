package pt.ulisboa.tecnico.socialsoftware.tutor.suggestions;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.suggestions.domain.Suggestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.suggestions.dto.SuggestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.suggestions.repository.SuggestionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.sql.SQLException;
import java.time.LocalDateTime;

@Service
public class SuggestionService {

    @Autowired
    private SuggestionRepository suggestionRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private UserRepository userRepository;

    @PersistenceContext
    EntityManager entityManager;

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public SuggestionDto createSuggestion(Integer studentId, Integer questionId, SuggestionDto suggestionDto) {
        User student = userRepository
                .findById(studentId)
                .orElseThrow(() -> new TutorException(ErrorMessage.USER_NOT_FOUND, studentId));

        Question question = questionRepository
                .findById(questionId)
                .orElseThrow(() -> new TutorException(ErrorMessage.QUESTION_NOT_FOUND, questionId));

        Suggestion suggestion = new Suggestion(student, question, suggestionDto);
        suggestion.setCreationDate(LocalDateTime.now());
        this.entityManager.persist(suggestion);
        return new SuggestionDto(suggestion);
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @org.springframework.transaction.annotation.Transactional(isolation = Isolation.REPEATABLE_READ)
    public SuggestionDto updateSuggestion(Integer suggestionId, SuggestionDto suggestionDto) {
        Suggestion suggestion = suggestionRepository
                .findById(suggestionId)
                .orElseThrow(() -> new TutorException(ErrorMessage.SUGGESTION_NOT_FOUND, suggestionId));

        suggestion.update(suggestionDto);
        return new SuggestionDto(suggestion);
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @org.springframework.transaction.annotation.Transactional(isolation = Isolation.REPEATABLE_READ)
    public void removeSuggestion(Integer suggestionId) {
        Suggestion suggestion = suggestionRepository
                .findById(suggestionId)
                .orElseThrow(() -> new TutorException(ErrorMessage.SUGGESTION_NOT_FOUND, suggestionId));

        suggestion.remove();
        entityManager.remove(suggestion);
    }
}
