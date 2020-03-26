package pt.ulisboa.tecnico.socialsoftware.tutor.suggestions;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.suggestions.domain.Suggestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.suggestions.dto.SuggestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.suggestions.repository.SuggestionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SuggestionService {

    @Autowired
    private SuggestionRepository suggestionRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public SuggestionDto createSuggestion(Integer studentId, Integer courseId, SuggestionDto suggestionDto) {
        User student = userRepository
                .findById(studentId)
                .orElseThrow(() -> new TutorException(ErrorMessage.USER_NOT_FOUND, studentId));

        Course course = courseRepository
                .findById(courseId)
                .orElseThrow(() -> new TutorException(ErrorMessage.COURSE_NOT_FOUND, courseId));

        if (suggestionDto.getCreationDate() == null) {
            suggestionDto.setCreationDate(LocalDateTime.now().format(Course.formatter));
            suggestionDto.getQuestion().setCreationDate(suggestionDto.getCreationDate());
        }

        Suggestion suggestion = new Suggestion(student, course, suggestionDto);
        suggestionRepository.save(suggestion);
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
            backoff = @Backoff(delay = 5000)
    )
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public SuggestionDto findSuggestionById(Integer suggestionId) {
        Suggestion suggestion = suggestionRepository.findById(suggestionId)
                .orElseThrow(() -> new TutorException(ErrorMessage.SUGGESTION_NOT_FOUND, suggestionId));

        return new SuggestionDto(suggestion);
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000)
    )
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<SuggestionDto> findSuggestionsByCourse(Integer courseId) {
        courseRepository
                .findById(courseId)
                .orElseThrow(() -> new TutorException(ErrorMessage.COURSE_NOT_FOUND, courseId));

        return suggestionRepository.findAll()
                .stream()
                .filter(suggestion -> suggestion.getQuestion().getCourse().getId() == courseId)
                .map(SuggestionDto::new)
                .collect(Collectors.toList());
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000)
    )
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<SuggestionDto> findSuggestionsByStudent(Integer studentId) {
        userRepository
                .findById(studentId)
                .orElseThrow(() -> new TutorException(ErrorMessage.USER_NOT_FOUND, studentId));

        return suggestionRepository.findAll()
                .stream()
                .filter(suggestion -> suggestion.getStudent().getId() == studentId)
                .map(SuggestionDto::new)
                .collect(Collectors.toList());
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
        suggestionRepository.delete(suggestion);
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @org.springframework.transaction.annotation.Transactional(isolation = Isolation.REPEATABLE_READ)
    public CourseDto getSuggestionCourse(Integer suggestionId) {
        Suggestion suggestion = suggestionRepository
                .findById(suggestionId)
                .orElseThrow(() -> new TutorException(ErrorMessage.SUGGESTION_NOT_FOUND, suggestionId));

        return new CourseDto(suggestion.getQuestion().getCourse());
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @org.springframework.transaction.annotation.Transactional(isolation = Isolation.REPEATABLE_READ)
    public UserDto getSuggestionUser(Integer suggestionId) {
        Suggestion suggestion = suggestionRepository
                .findById(suggestionId)
                .orElseThrow(() -> new TutorException(ErrorMessage.SUGGESTION_NOT_FOUND, suggestionId));

        return new UserDto(suggestion.getStudent());
    }
}
