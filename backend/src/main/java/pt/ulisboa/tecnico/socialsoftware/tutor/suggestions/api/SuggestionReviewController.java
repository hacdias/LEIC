package pt.ulisboa.tecnico.socialsoftware.tutor.suggestions.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.suggestions.SuggestionService;
import pt.ulisboa.tecnico.socialsoftware.tutor.suggestions.SuggestionReviewService;
import pt.ulisboa.tecnico.socialsoftware.tutor.suggestions.dto.SuggestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.suggestions.dto.SuggestionReviewDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import javax.validation.Valid;
import java.util.List;

@RestController
public class SuggestionReviewController {

    @Autowired
    private SuggestionReviewService suggestionReviewService;

    @GetMapping("/suggestions/{suggestionId}/suggestionReviews")
    @PreAuthorize("(hasRole('ROLE_STUDENT') and hasPermission(#suggestionId, 'SUGGESTION.AUTHOR')) or" +
            "(hasRole('ROLE_TEACHER') and hasPermission(#suggestionId, 'SUGGESTION.ACCESS'))")
    public List<SuggestionReviewDto> getSuggestionReviewsBySuggestion(@PathVariable Integer suggestionId) {
        return suggestionReviewService.findSuggestionReviewsBySuggestion(suggestionId);
    }

    @GetMapping("/suggestionReviews")
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    public List<SuggestionReviewDto> getSuggestionReviews(Authentication authentication) {
        Integer teacherId = ((User) authentication.getPrincipal()).getId();
        return suggestionReviewService.findSuggestionReviewsByTeacher(teacherId);
    }

    @GetMapping("/suggestionReviews/{suggestionReviewId}")
    @PreAuthorize("(hasRole('ROLE_STUDENT') and hasPermission(#suggestionReviewId, 'SUGGESTIONREVIEW.RECEPTOR')) or" +
            "(hasRole('ROLE_TEACHER') and hasPermission(#suggestionReviewId, 'SUGGESTIONREVIEW.ACCESS'))")
    public SuggestionReviewDto getSuggestionReview(@PathVariable Integer suggestionReviewId) {
        return suggestionReviewService.findSuggestionReviewById(suggestionReviewId);
    }

    @PostMapping("/suggestions/{suggestionId}/suggestionReviews")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#suggestionId, 'SUGGESTION.ACCESS')")
    public SuggestionReviewDto createSuggestionReview(Authentication authentication, @PathVariable int suggestionId, @Valid @RequestBody SuggestionReviewDto suggestionReview) {
        Integer teacherId = ((User) authentication.getPrincipal()).getId();
        return suggestionReviewService.createSuggestionReview(teacherId, suggestionId, suggestionReview);
    }

    @PutMapping("/suggestionReviews/{suggestionReviewId}")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#suggestionReviewId, 'SUGGESTIONREVIEW.ACCESS')")
    public SuggestionReviewDto updateSuggestionReview(@PathVariable Integer suggestionReviewId, @Valid @RequestBody SuggestionReviewDto suggestionReview) {
        return suggestionReviewService.updateSuggestionReview(suggestionReviewId, suggestionReview);
    }

    @DeleteMapping("/suggestionReviews/{suggestionReviewId}")
    @PreAuthorize("(hasRole('ROLE_TEACHER') and hasPermission(#suggestionReviewId, 'SUGGESTIONREVIEW.ACCESS'))")
    public ResponseEntity removeSuggestionReview(@PathVariable Integer suggestionReviewId) {
        suggestionReviewService.removeSuggestionReview(suggestionReviewId);
        return ResponseEntity.ok().build();
    }

}
