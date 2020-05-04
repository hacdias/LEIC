package pt.ulisboa.tecnico.socialsoftware.tutor.query.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.query.AnswerQueryService;
import pt.ulisboa.tecnico.socialsoftware.tutor.query.dto.AnswerQueryDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import javax.validation.Valid;
import java.io.IOException;
import java.security.Principal;
import java.util.List;

@RestController
public class AnswerQueryController {
    private static Logger logger = LoggerFactory.getLogger(AnswerQueryController.class);

    private AnswerQueryService answerQueryService;

    AnswerQueryController(AnswerQueryService answerQueryService) { this.answerQueryService = answerQueryService; }

    @GetMapping("/query/{queryId}/answers")
    @PreAuthorize("hasPermission(#queryId, 'QUERY.ACCESS')")
    public List<AnswerQueryDto> getAnswerToQuery(@PathVariable int queryId) {
        return this.answerQueryService.getAnswersToQuery(queryId);
    }

    @GetMapping("/user/answers-queries")
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    public List<AnswerQueryDto> getAnswersByTeacher(Principal principal) {
        User teacher = (User) ((Authentication) principal).getPrincipal();
        return this.answerQueryService.getAnswersByTeacher(teacher.getId());
    }

    @PostMapping("/query/{queryId}/answers")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#queryId, 'QUERY.ACCESS')")
    public AnswerQueryDto createAnswerQuery(Principal principal, @PathVariable int queryId, @Valid @RequestBody AnswerQueryDto answerQueryDto) {
        User teacher = (User) ((Authentication) principal).getPrincipal();
        return this.answerQueryService.createAnswerQuery(queryId, teacher.getId(), answerQueryDto);
    }

    @PostMapping("/answer-queries/{answerQueryId}/further-clarification")
    @PreAuthorize("hasPermission(#queryId, 'ANSWER.QUERY.ACCESS')")
    public AnswerQueryDto createFurtherClarification(Principal principal, @PathVariable int answerQueryId, @Valid @RequestBody AnswerQueryDto furtherClarificationDto) {
        User user = (User) ((Authentication) principal).getPrincipal();
        return this.answerQueryService.addFurtherClarification(answerQueryId, user.getId(), furtherClarificationDto);
    }

    @PutMapping("/answer-queries/{answerQueryId}")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#answerQueryId, 'ANSWER.QUERY.ALTER')")
    public AnswerQueryDto updateAnswerQuery(@PathVariable Integer answerQueryId, @Valid @RequestBody AnswerQueryDto answerQueryDto) {
        return this.answerQueryService.updateAnswerQuery(answerQueryId, answerQueryDto);
    }

    @DeleteMapping("/answer-queries/{answerQueryId}")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#answerQueryId, 'ANSWER.QUERY.ALTER')")
    public ResponseEntity removeAnswerQuery(@PathVariable Integer answerQueryId) throws IOException {
        logger.debug("removeAnswerQuery answerQueryId: {}: ", answerQueryId);
        AnswerQueryDto answerQueryDto = answerQueryService.findAnswerQueryById(answerQueryId);
        answerQueryService.removeAnswerQuery(answerQueryId);

        return ResponseEntity.ok().build();
    }
}
