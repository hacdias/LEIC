package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.service

//-----------External Imports-----------
import spock.lang.Specification
import org.springframework.context.annotation.Bean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest

//-----------Internal Imports-----------
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentService
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.Tournament
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.repository.TournamentRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository



@DataJpaTest
class RemoveTournamentTest extends Specification{
    public static final String COURSE_NAME = "Software Architecture"
    public static final String ACRONYM = "AS1"
    public static final String ACADEMIC_TERM = "1 SEM"
    public static final String QUESTION_TITLE = 'a question title'
    public static final String QUESTION_CONTENT = 'a question content'
    public static final String OPTION_CONTENT = "option id content"
    public static final String STUDENT_NAME = "Anonymous User"
    public static final String STUDENT_USERNAME = "anon"
}

@Autowired
CourseRepository courseRepository

@Autowired
CourseExecutionRepository courseExecutionRepository

@Autowired
TournamentExecutionRepository tournamentExecutionRepository

@Autowired
TournamentService tournamentService

@Autowired
UserRepository userRepository

def course
def courseExecution
def student
def tournament


    def setup() {

        course = new Course(COURSE_NAME, Course.Type.TECNICO)
        courseRepository.save(course)

        courseExecution = new CourseExecution(course, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO)
        courseExecutionRepository.save(courseExecution)

        student = new User(STUDENT_NAME, STUDENT_USERNAME, 1, User.Role.STUDENT)
        student.getCourseExecutions().add(courseExecution)
        courseExecution.getUsers().add(student)
        userRepository.save(student)


        tournament = new Tournamet()
        tournament.setKey(1)
        tournament.setStudent(student)
        tournament.setCourseExecution(courseExecution)
        tournamentRepository.save(tournament)
    }

    def "remove a tournament that is already concluded"() {
        given: "an close tournament"
        tournament.setCreated(true)
        tournamentRepository.save(tournament)

        when:
        tournamentService.removeTournament(tournament.getId())

        then: "the tournament is removed"
        TournamentRepository.count() == 0L
    }

    def "remove a tournament"() {
        when:
        tournamentService.removeTournament(tournament.getId())

        then: "the tournament is removed"
        TournamentRepository.count() == 0L
    }

@TestConfiguration
static class TournamentServiceImplTestContextConfiguration {
    @Bean
    TournamentService tournamentService(){
        return new TournamentService()
    }
}
