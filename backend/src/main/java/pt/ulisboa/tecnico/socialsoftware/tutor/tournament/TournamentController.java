package pt.ulisboa.tecnico.socialsoftware.tutor.tournament;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto.TournamentDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
public class TournamentController {

    @Autowired
    private TournamentService tournamentService;  

    @GetMapping("/executions/{executionId}/tournaments")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#executionId, 'EXECUTION.ACCESS')")
    public List<TournamentDto> getOpenTournaments(Authentication authentication, @PathVariable int executionId) {
        Integer studentId = ((User) authentication.getPrincipal()).getId();
        
        return tournamentService.getOpenTournaments(studentId, executionId);
    }

    @PostMapping("tournaments/{TournamentId}")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public TournamentDto addEnrolledStudentToTournament(Authentication authentication, @PathVariable int TournamentId) {
        Integer studentId = ((User) authentication.getPrincipal()).getId();

        return tournamentService.addEnrolledStudentToTournament(studentId, TournamentId);
    }

    @PostMapping("/executions/{executionId}/tournaments")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#executionId, 'EXECUTION.ACCESS')")
    public TournamentDto createTournament(Authentication authentication, @PathVariable int executionId, @Valid @RequestBody TournamentDto tournament) {
        Integer studentId = ((User) authentication.getPrincipal()).getId();

        return tournamentService.createTournament(executionId, studentId, tournament);
    }

    @DeleteMapping("/tourmaments/{tournamentId}")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#tournamentId, 'TOURNAMENT.CREATOR')")
    public ResponseEntity deleteTournament(Authentication authentication, @PathVariable Integer tournamentId) {
        Integer studentId = ((User) authentication.getPrincipal()).getId();

        tournamentService.removeTournament(studentId, tournamentId);

        return ResponseEntity.ok().build();
    }
}