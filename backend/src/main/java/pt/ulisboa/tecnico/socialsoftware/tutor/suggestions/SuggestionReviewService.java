package pt.ulisboa.tecnico.socialsoftware.tutor.suggestions;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.suggestions.domain.Suggestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.suggestions.domain.SuggestionReview;
import pt.ulisboa.tecnico.socialsoftware.tutor.suggestions.dto.SuggestionReviewDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.suggestions.repository.SuggestionReviewRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.suggestions.repository.SuggestionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.sql.SQLException;
import java.time.LocalDateTime;

@Service
public class SuggestionReviewService {

    @Autowired
    private SuggestionRepository suggestionRepository;

    @Autowired
    private SuggestionReviewRepository suggestionReviewRepository;

    @Autowired
    private UserRepository userRepository;

    @PersistenceContext
    EntityManager entityManager;

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public SuggestionReviewDto createSuggestionReview(Integer teacherId, Integer suggestionId, SuggestionReviewDto suggestionReviewDto) {
        User teacher = userRepository
                .findById(teacherId)
                .orElseThrow(() -> new TutorException(ErrorMessage.USER_NOT_FOUND, teacherId));

        Suggestion suggestion = suggestionRepository
                .findById(suggestionId)
                .orElseThrow(() -> new TutorException(ErrorMessage.SUGGESTION_NOT_FOUND, suggestionId));

        SuggestionReview suggestionReview = new SuggestionReview(teacher, suggestion, suggestionReviewDto);
        suggestionReview.setCreationDate(LocalDateTime.now());

        if (!suggestionReview.getSuggestion().getApproved() && suggestionReview.getApproved()) {
            suggestionReview.getSuggestion().setApproved(suggestionReviewDto.getApproved());
        }

        this.entityManager.persist(suggestionReview);
        return new SuggestionReviewDto(suggestionReview);
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @org.springframework.transaction.annotation.Transactional(isolation = Isolation.REPEATABLE_READ)
    public SuggestionReviewDto updateSuggestionReview(Integer suggestionReviewId, SuggestionReviewDto suggestionReviewDto) {
        SuggestionReview suggestionReview = suggestionReviewRepository
                .findById(suggestionReviewId)
                .orElseThrow(() -> new TutorException(ErrorMessage.SUGGESTION_REVIEW_NOT_FOUND, suggestionReviewId));

        suggestionReview.setJustification(suggestionReviewDto.getJustification());
        suggestionReview.setApproved(suggestionReviewDto.getApproved());
        if (!suggestionReview.getSuggestion().getApproved() && suggestionReview.getApproved()) {
            suggestionReview.getSuggestion().setApproved(suggestionReviewDto.getApproved());
        }

        return new SuggestionReviewDto(suggestionReview);
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @org.springframework.transaction.annotation.Transactional(isolation = Isolation.REPEATABLE_READ)
    public void removeSuggestionReview(Integer suggestionReviewId) {
        SuggestionReview suggestionReview = suggestionReviewRepository
                .findById(suggestionReviewId)
                .orElseThrow(() -> new TutorException(ErrorMessage.SUGGESTION_REVIEW_NOT_FOUND, suggestionReviewId));

        suggestionReview.remove();
        entityManager.remove(suggestionReview);
    }
}