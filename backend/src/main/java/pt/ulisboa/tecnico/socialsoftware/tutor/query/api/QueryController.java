package pt.ulisboa.tecnico.socialsoftware.tutor.query.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.query.QueryService;
import pt.ulisboa.tecnico.socialsoftware.tutor.query.dto.QueryDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import javax.validation.Valid;
import java.io.IOException;
import java.security.Principal;
import java.util.List;

@RestController
public class QueryController {
    private static Logger logger = LoggerFactory.getLogger(QueryController.class);

    private QueryService queryService;

    QueryController(QueryService queryService) {
        this.queryService = queryService;
    }

    @GetMapping("/question/{questionId}/queries")
    @PreAuthorize("hasPermission(#questionId, 'QUESTION.ACCESS')")
    public List<QueryDto> getQueriesToQuestion(@PathVariable int questionId) {
        return this.queryService.getQueriesToQuestion(questionId);
    }

    @GetMapping("/user/queries")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public List<QueryDto> getQueriesByStudent(Principal principal) {
        User student = (User) ((Authentication) principal).getPrincipal();
        return this.queryService.getQueriesByStudent(student.getId());
    }

    @GetMapping("/teacher/queriesInCourses")
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    public List<QueryDto> getQueriesInTeachersCourse(Principal principal) {
        User teacher = (User) ((Authentication) principal).getPrincipal();
        return this.queryService.getQueriesByStudent(teacher.getId());
    }

    @PostMapping("/question/{questionId}/queries")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#questionId, 'QUESTION.ACCESS')")
    public QueryDto createQuery(Principal principal, @PathVariable int questionId, @Valid @RequestBody QueryDto queryDto) {
        User student = (User) ((Authentication) principal).getPrincipal();
        return this.queryService.createQuery(questionId, student.getId(), queryDto);
    }

    @PutMapping("/queries/{queryId}")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#queryId, 'QUERY.ACCESS')")
    public QueryDto updateQuery(@PathVariable Integer queryId, @Valid @RequestBody QueryDto queryDto) {
        return this.queryService.updateQuery(queryId, queryDto);
    }

    @DeleteMapping("/queries/{queryId}")
    @PreAuthorize("hasPermission(#queryId, 'QUERY.ACCESS')")
    public ResponseEntity removeQuery(@PathVariable Integer queryId) throws IOException {
        logger.debug("removeQuery queryId: {}: ", queryId);
        QueryDto queryDto = queryService.findQueryById(queryId);
        queryService.removeQuery(queryId);

        return ResponseEntity.ok().build();
    }
}
