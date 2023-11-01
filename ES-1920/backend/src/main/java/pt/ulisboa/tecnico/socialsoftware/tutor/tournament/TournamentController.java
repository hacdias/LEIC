package pt.ulisboa.tecnico.socialsoftware.tutor.tournament;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto.TournamentDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto.StatementQuizDto;

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

    @GetMapping("/executions/{executionId}/enrolled-tournaments")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#executionId, 'EXECUTION.ACCESS')")
    public List<TournamentDto> getEnrolledTournaments(Authentication authentication, @PathVariable int executionId) {
        Integer studentId = ((User) authentication.getPrincipal()).getId();

        return tournamentService.getEnrolledTournaments(studentId, executionId);
    }

    @GetMapping("/executions/{executionId}/created-tournaments")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#executionId, 'EXECUTION.ACCESS')")
    public List<TournamentDto> getCreatedTournaments(Authentication authentication, @PathVariable int executionId) {
        Integer studentId = ((User) authentication.getPrincipal()).getId();

        return tournamentService.getCreatedTournaments(studentId, executionId);
    }

    @GetMapping("/tournaments/{tournamentId}/get-quiz")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public StatementQuizDto getTournamentQuiz(Authentication authentication, @PathVariable int tournamentId) {
        Integer studentId = ((User) authentication.getPrincipal()).getId();

        return tournamentService.getTournamentQuiz(studentId, tournamentId);
    }

    @PostMapping("/tournaments/{tournamentId}")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public TournamentDto addEnrolledStudentToTournament(Authentication authentication, @PathVariable int tournamentId) {
        Integer studentId = ((User) authentication.getPrincipal()).getId();

        return tournamentService.addEnrolledStudentToTournament(studentId, tournamentId);
    }

    @PostMapping("/tournaments/{tournamentId}/generate")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public TournamentDto generateTournamentQuiz(Authentication authentication, @PathVariable int tournamentId) {

        return tournamentService.generateTournamentQuiz(tournamentId);
    }

    @PostMapping("/executions/{executionId}/tournaments")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#executionId, 'EXECUTION.ACCESS')")
    public TournamentDto createTournament(Authentication authentication, @PathVariable int executionId, @Valid @RequestBody TournamentDto tournament) {
        Integer studentId = ((User) authentication.getPrincipal()).getId();

        formatDates(tournament);

        return tournamentService.createTournament(executionId, studentId, tournament);
    }

    @DeleteMapping("/tournaments/{tournamentId}")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#tournamentId, 'TOURNAMENT.CREATOR')")
    public ResponseEntity deleteTournament(Authentication authentication, @PathVariable int tournamentId)  {
        Integer studentId = ((User) authentication.getPrincipal()).getId();

        tournamentService.removeTournament(studentId, tournamentId);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/tournaments/toggle-privacy")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public void togglePrivacy(Authentication authentication) {
        Integer studentId = ((User) authentication.getPrincipal()).getId();
        tournamentService.toggleStatsPrivacy(studentId);
    }

    private void formatDates(TournamentDto tournament) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        if (tournament.getAvailableDate() != null && !tournament.getAvailableDate().matches("(\\d{4})-(\\d{2})-(\\d{2}) (\\d{2}):(\\d{2})")) {
            tournament.setAvailableDate(LocalDateTime.parse(tournament.getAvailableDate().replaceAll(".$", ""), DateTimeFormatter.ISO_DATE_TIME).format(formatter));
        }

        if (tournament.getConclusionDate() != null && !tournament.getConclusionDate().matches("(\\d{4})-(\\d{2})-(\\d{2}) (\\d{2}):(\\d{2})")) {
            tournament.setConclusionDate(LocalDateTime.parse(tournament.getConclusionDate().replaceAll(".$", ""), DateTimeFormatter.ISO_DATE_TIME).format(formatter));
        }
    }
}