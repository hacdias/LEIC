package pt.ulisboa.tecnico.socialsoftware.tutor.suggestions;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.suggestions.domain.Suggestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.suggestions.dto.SuggestionReviewDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.suggestions.repository.SuggestionRepository;

import java.sql.SQLException;

@Service
public class SuggestionReviewService {

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public SuggestionReviewDto createSuggestionReview(Integer teacherId, Integer suggestionId, SuggestionReviewDto suggestionReviewDto) {
        return null;
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @org.springframework.transaction.annotation.Transactional(isolation = Isolation.REPEATABLE_READ)
    public SuggestionReviewDto updateSuggestionReview(Integer suggestionReviewId, SuggestionReviewDto suggestionReviewDto) {
        return null;
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @org.springframework.transaction.annotation.Transactional(isolation = Isolation.REPEATABLE_READ)
    public void removeSuggestionReview(Integer suggestionReviewId) {
        // EMPTY
    }
}