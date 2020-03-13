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
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.TopicRepository;

@Service
public class TournamentService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseExecutionRepository courseExecutionRepository;

    @Autowired
    private TournamentRepository tournamentRepository;  
    
    @Autowired
    private TopicRepository topicRepository;  

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
        
        User student = userRepository.findById(studentId).orElseThrow(() -> new TutorException(ErrorMessage.USER_NOT_FOUND, studentId));
        CourseExecution courseExecution = courseExecutionRepository.findById(executionId).orElseThrow(() -> new TutorException(ErrorMessage.COURSE_EXECUTION_NOT_FOUND, executionId));

        if (student.getRole() != User.Role.STUDENT) {
            throw new TutorException(ErrorMessage.USER_NOT_STUDENT, studentId);
        }

        if (!student.getCourseExecutions().contains(courseExecution)) {
            throw new TutorException(ErrorMessage.USER_NOT_ENROLLED, student.getUsername());     //TO DO: check exception
        }

        if (tournamentDto.getKey() == null) {
            tournamentDto.setKey(getMaxTournamentKey() + 1);
        }

        Tournament tournament = new Tournament(student, tournamentDto);
        tournament.setCourseExecution(courseExecution);

        Topic topic;
        if (tournamentDto.getTopics() != null) {
            for (TopicDto topicDto : tournamentDto.getTopics()) {
                topic = topicRepository.findById(topicDto.getId())
                        .orElseThrow(() -> new TutorException(ErrorMessage.TOPIC_NOT_FOUND, topicDto.getId()));
                tournament.addTopic(topic);
            }
        } //else throw exception, needs to have at least one topic, probably will test in invalid inputs test

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
    public void removeTournament(Integer studentId, Integer tournamentId) { 
        Tournament tournament = tournamentRepository.findById(tournamentId).orElseThrow(() -> new TutorException(ErrorMessage.TOURNAMENT_NOT_FOUND, tournamentId));
        User student = userRepository.findById(studentId).orElseThrow(() -> new TutorException(ErrorMessage.USER_NOT_FOUND, studentId));

        if (student.getRole() != User.Role.STUDENT) {
            throw new TutorException(ErrorMessage.USER_NOT_STUDENT, studentId); //TO DO: check
        }

        if (!student.getEnrolledTournaments().contains(tournament)) {
            throw new TutorException(ErrorMessage.STUDENT_NOT_ENROLLED, studentId, tournamentId);
        }

        if (tournament.getStudent() != student) {
            throw new TutorException(ErrorMessage.STUDENT_NOT_CREATOR, studentId, tournamentId);
        }

        tournament.remove();
        entityManager.remove(tournament);
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @org.springframework.transaction.annotation.Transactional(isolation = Isolation.REPEATABLE_READ)
    public void addEnrolledStudentToTournament(Integer studentId, Integer tournamentId) { 
        Tournament tournament = tournamentRepository.findById(tournamentId).orElseThrow(() -> new TutorException(ErrorMessage.TOURNAMENT_NOT_FOUND, tournamentId));
        User student = userRepository.findById(studentId).orElseThrow(() -> new TutorException(ErrorMessage.USER_NOT_FOUND, studentId));

        if (student.getRole() != User.Role.STUDENT) {
            throw new TutorException(ErrorMessage.USER_NOT_STUDENT, studentId);
        }
        
        tournament.addEnrolledStudent(student);
    }
}