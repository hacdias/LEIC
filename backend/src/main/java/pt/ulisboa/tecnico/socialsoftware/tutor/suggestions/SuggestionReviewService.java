package pt.ulisboa.tecnico.socialsoftware.tutor.suggestions;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.suggestions.domain.Suggestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.suggestions.domain.SuggestionReview;
import pt.ulisboa.tecnico.socialsoftware.tutor.suggestions.dto.SuggestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.suggestions.dto.SuggestionReviewDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.suggestions.repository.SuggestionReviewRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.suggestions.repository.SuggestionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SuggestionReviewService {

    @Autowired
    private SuggestionRepository suggestionRepository;

    @Autowired
    private SuggestionReviewRepository suggestionReviewRepository;

    @Autowired
    private UserRepository userRepository;

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

        suggestionReviewRepository.save(suggestionReview);
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
            backoff = @Backoff(delay = 5000)
    )
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public SuggestionReviewDto findSuggestionReviewById(Integer suggestionReviewId) {
        SuggestionReview suggestionReview = suggestionReviewRepository.findById(suggestionReviewId)
                .orElseThrow(() -> new TutorException(ErrorMessage.SUGGESTION_REVIEW_NOT_FOUND, suggestionReviewId));

        return new SuggestionReviewDto(suggestionReview);
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000)
    )
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<SuggestionReviewDto> findSuggestionReviewsBySuggestion(Integer suggestionId) {
        Suggestion suggestion = suggestionRepository
                .findById(suggestionId)
                .orElseThrow(() -> new TutorException(ErrorMessage.SUGGESTION_NOT_FOUND, suggestionId));

        return suggestionReviewRepository.findAll()
                .stream()
                .filter(suggestionReview -> suggestionReview.getSuggestion().getId() == suggestionId)
                .map(SuggestionReviewDto::new)
                .collect(Collectors.toList());
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000)
    )
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<SuggestionReviewDto> findSuggestionReviewsByTeacher(Integer teacherId) {
        userRepository
                .findById(teacherId)
                .orElseThrow(() -> new TutorException(ErrorMessage.USER_NOT_FOUND, teacherId));

        return suggestionReviewRepository.findAll()
                .stream()
                .filter(suggestionReview -> suggestionReview.getTeacher().getId() == teacherId)
                .map(SuggestionReviewDto::new)
                .collect(Collectors.toList());
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
        suggestionReviewRepository.delete(suggestionReview);
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @org.springframework.transaction.annotation.Transactional(isolation = Isolation.REPEATABLE_READ)
    public CourseDto getSuggestionReviewCourse(Integer suggestionReviewId) {
        SuggestionReview suggestionReview = suggestionReviewRepository
                .findById(suggestionReviewId)
                .orElseThrow(() -> new TutorException(ErrorMessage.SUGGESTION_REVIEW_NOT_FOUND, suggestionReviewId));

        return new CourseDto(suggestionReview.getSuggestion().getQuestion().getCourse());
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @org.springframework.transaction.annotation.Transactional(isolation = Isolation.REPEATABLE_READ)
    public UserDto getSuggestionReviewReceptor(Integer suggestionReviewId) {
        SuggestionReview suggestionReview = suggestionReviewRepository
                .findById(suggestionReviewId)
                .orElseThrow(() -> new TutorException(ErrorMessage.SUGGESTION_REVIEW_NOT_FOUND, suggestionReviewId));

        return new UserDto(suggestionReview.getSuggestion().getStudent());
    }
}