package pt.ulisboa.tecnico.socialsoftware.tutor.tournament;

//--------External Imports--------
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

//--------Internal Imports--------
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto.TournamentDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.Tournament;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.repository.TournamentRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository;
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;
@Service
public class TournamentService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseExecutionRepository courseExecutionRepository;

    @Autowired
    private TournamentRepository tournamentRepository;  

    @PersistenceContext
    EntityManager entityManager;

    public Integer getMaxTournamentKey() {
        Integer maxTournamentKey = tournamentRepository.getMaxTournamentKey();
        return maxTournamentKey != null ? maxTournamentKey : 0;
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public TournamentDto createTournament(Integer executionId, Integer studentId, TournamentDto tournamentDto) {
        
        User student = userRepository.findById(studentId).orElseThrow(() -> new TutorException(USER_NOT_FOUND, studentId));
        CourseExecution courseExecution = courseExecutionRepository.findById(executionId).orElseThrow(() -> new TutorException(COURSE_EXECUTION_NOT_FOUND, executionId));

        if (student.getRole() != User.Role.STUDENT) {
            //throw new TutorException(USER_NOT_STUDENT, studentId);  TO DO: Add exception
        }

        if (!student.getCourseExecutions().contains(courseExecution)) {
            throw new TutorException(USER_NOT_ENROLLED, student.getUsername());     //TO DO: check exception
        }

        if (tournamentDto.getKey() == null) {
            tournamentDto.setKey(getMaxTournamentKey() + 1);
        }

        Tournament tournament = new Tournament(student, tournamentDto);
        //tournament.setCourseExecution(courseExecution);

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
    public void removeTournament(Integer tournamentId) { //TO DO MENSAGEM DE ERRO
        Tournament tournament = tournamentRepository.findById(tournamentId).orElseThrow(() -> new TutorException(ErrorMessage.ACCESS_DENIED, tournamentId));

        tournament.remove();
        entityManager.remove(tournament);
    }


    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public TournamentDto updateTournament(Integer tournamentId, TournamentDto tournamentDto) {
        //TO DO: implement
        return null;
    }
}