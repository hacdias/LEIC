package pt.ulisboa.tecnico.socialsoftware.tutor.suggestions.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.suggestions.SuggestionService;
import pt.ulisboa.tecnico.socialsoftware.tutor.suggestions.dto.SuggestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import javax.validation.Valid;
import java.util.List;

@RestController
public class SuggestionController {

    @Autowired
    private SuggestionService suggestionService;

    @GetMapping("/courses/{courseId}/suggestions")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#courseId, 'COURSE.ACCESS')")
    public List<SuggestionDto> getSuggestionsByCourse(@PathVariable Integer courseId) {
        return suggestionService.findSuggestionsByCourse(courseId);
    }

    @GetMapping("/suggestions")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public List<SuggestionDto> getSuggestions(Authentication authentication) {
        Integer studentId = ((User) authentication.getPrincipal()).getId();
        return suggestionService.findSuggestionsByStudent(studentId);
    }

    @GetMapping("/suggestions/{suggestionId}")
    @PreAuthorize("(hasRole('ROLE_STUDENT') and hasPermission(#suggestionId, 'SUGGESTION.AUTHOR')) or" +
        "(hasRole('ROLE_TEACHER') and hasPermission(#suggestionId, 'SUGGESTION.ACCESS'))")
    public SuggestionDto getSuggestion(@PathVariable Integer suggestionId) {
        return suggestionService.findSuggestionById(suggestionId);
    }

    @PostMapping("/courses/{courseId}/suggestions")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#courseId, 'COURSE.ACCESS')")
    public SuggestionDto createSuggestion(Authentication authentication, @PathVariable int courseId, @Valid @RequestBody SuggestionDto suggestion) {
        Integer studentId = ((User) authentication.getPrincipal()).getId();
        suggestion.setApproved(false);
        suggestion.getQuestion().setStatus(Question.Status.PENDING_APPROVAL.name());
        return suggestionService.createSuggestion(studentId, courseId, suggestion);
    }

    @PutMapping("/suggestions/{suggestionId}")
    @PreAuthorize("(hasRole('ROLE_STUDENT') and hasPermission(#suggestionId, 'SUGGESTION.AUTHOR')) or" +
            "(hasRole('ROLE_TEACHER') and hasPermission(#suggestionId, 'SUGGESTION.ACCESS'))")
    public SuggestionDto updateSuggestion(Authentication authentication, @PathVariable Integer suggestionId, @Valid @RequestBody SuggestionDto suggestion) {
        User.Role role = ((User) authentication.getPrincipal()).getRole();
        if (role == User.Role.STUDENT) {
            suggestion.setApproved(false);
        }
        return suggestionService.updateSuggestion(suggestionId, suggestion);
    }

    @DeleteMapping("/suggestions/{suggestionId}")
    @PreAuthorize("(hasRole('ROLE_STUDENT') and hasPermission(#suggestionId, 'SUGGESTION.AUTHOR')) or" +
            "(hasRole('ROLE_TEACHER') and hasPermission(#suggestionId, 'SUGGESTION.ACCESS'))")
    public ResponseEntity removeQuestion(@PathVariable Integer suggestionId) {
        suggestionService.removeSuggestion(suggestionId);
        return ResponseEntity.ok().build();
    }
    
}
