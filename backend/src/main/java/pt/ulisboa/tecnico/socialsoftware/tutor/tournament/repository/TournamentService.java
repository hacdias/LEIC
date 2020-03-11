package pt.ulisboa.tecnico.socialsoftware.tutor.tournament;

//--------External Imports--------
import java.sql.SQLException;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.format.DateTimeFormatter;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

//--------Internal Imports--------
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto.TournamentDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.Tournament;



@Service
public class TournamentService {

    //@Autowired
    //private TournamentRepository tournamentRepository;    TO DO: create tournamentRepository

    @PersistenceContext
    EntityManager entityManager;

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public TournamentDto createTournament(int executionId, TournamentDto tournamentDto) {
        //TO DO: executionId will probably be necessary

        if (tournamentDto.getKey() == null) {
           // tournamentDto.setKey(getMaxTournamentKey() + 1);       TO DO: implement getMaxTournamentKey
        }
        Tournament tournament = new Tournament(tournamentDto);
        //tournament.setCourseExecution(courseExecution);  TO DO: executionId will probably be necessary

        if (tournamentDto.getCreationDate() == null) {
            tournament.setCreationDate(LocalDateTime.now());
        } else {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            tournament.setCreationDate(LocalDateTime.parse(tournamentDto.getCreationDate(), formatter));
        }

        this.entityManager.persist(tournament);
        return new TournamentDto(tournament);
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @org.springframework.transaction.annotation.Transactional(isolation = Isolation.REPEATABLE_READ)
    public void removeTournament(Integer tournamentId) {

        tournament.remove();
        this.entityManager.remove(tournament);
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public TournamentDto updateTournament(Integer quizId, TournamentDto tournamentDto) {
        //TO DO: implement
        return null;
    }
}