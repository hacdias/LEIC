package pt.ulisboa.tecnico.socialsoftware.tutor.suggestions.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.suggestions.SuggestionService;
import pt.ulisboa.tecnico.socialsoftware.tutor.suggestions.dto.SuggestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import javax.validation.Valid;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Objects;

@RestController
public class SuggestionController {

    @Autowired
    private SuggestionService suggestionService;

    @Value("${figures.dir}")
    private String figuresDir;

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

    @PutMapping("/suggestions/{suggestionId}/image")
    @PreAuthorize("(hasRole('ROLE_STUDENT') and hasPermission(#suggestionId, 'SUGGESTION.AUTHOR')) or" +
            "(hasRole('ROLE_TEACHER') and hasPermission(#suggestionId, 'SUGGESTION.ACCESS'))")
    public String uploadImage(@PathVariable Integer suggestionId, @RequestParam("file") MultipartFile file) throws IOException {
        SuggestionDto suggestionDto = suggestionService.findSuggestionById(suggestionId);
        QuestionDto questionDto = suggestionDto.getQuestion();
        String url = questionDto.getImage() != null ? questionDto.getImage().getUrl() : null;
        if (url != null && Files.exists(getTargetLocation(url))) {
            Files.delete(getTargetLocation(url));
        }

        int lastIndex = Objects.requireNonNull(file.getContentType()).lastIndexOf('/');
        String type = file.getContentType().substring(lastIndex + 1);

        suggestionService.uploadImage(suggestionId, type);

        url = suggestionService.findSuggestionById(suggestionId).getQuestion().getImage().getUrl();
        Files.copy(file.getInputStream(), getTargetLocation(url), StandardCopyOption.REPLACE_EXISTING);
        return url;
    }

    private Path getTargetLocation(String url) {
        String fileLocation = figuresDir + url;
        return Paths.get(fileLocation);
    }
}
