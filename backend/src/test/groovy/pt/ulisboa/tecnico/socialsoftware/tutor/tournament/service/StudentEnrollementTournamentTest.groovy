package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.service

//-----------External Imports-----------
import spock.lang.Specification
import org.springframework.context.annotation.Bean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest

//-----------Internal Imports-----------
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentService
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.Tournament
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.repository.TournamentRepository


@DataJpaTest
public class StudentTournamentTest extends Specification {
    public static final String COURSE_NAME = "Software Architecture"
    public static final String ACRONYM = "AS1"
    public static final String ACADEMIC_TERM = "1 SEM"
    public static final String USER_NAME = "User"
    public static final String USER_USERNAME = "Username"
    public static final String USER_NAME2 = "User2"
    public static final String USER_USERNAME2 = "Username2"
    public static final String TOURNAMENT_TITLE = "TOURNAMENT 1"

    @Autowired
    CourseRepository courseRepository

    @Autowired
    CourseExecutionRepository courseExecutionRepository

    @Autowired
    TournamentRepository tournamentRepository

    @Autowired
    TournamentService tournamentService

    @Autowired
    UserRepository userRepository

    def course
    def courseExecution
    def tournament
    
    def setup() {

        course = new Course(COURSE_NAME, Course.Type.TECNICO)
        courseRepository.save(course)

        courseExecution = new CourseExecution(course, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO)
        courseExecutionRepository.save(courseExecution)

        tournament = new Tournament()
        tournament.setKey(1)
        tournament.setCourseExecution(courseExecution)
        courseExecution.addTournament(tournament)

        tournamentRepository.save(tournament)

    }

    def "student enrolls in tournament" () {
        given: "a student"
        def student = new User(USER_NAME, USER_USERNAME, 1, User.Role.STUDENT)
        student.getCourseExecutions().add(courseExecution)
        courseExecution.getUsers().add(student)
        userRepository.save(student)

        when:
        tournamentService.addEnrolledStudentToTournament(student.getId(), tournament.getId())

        then: "student is enrolled in tournament"
        def result = tournamentRepository.findAll().get(0)
        student.getEnrolledTournaments().contains(result)
        result.getEnrolledStudents().contains(student)

    }

    def "two students enroll in tournament" () {
        given: "two student"
        def student = new User(USER_NAME, USER_USERNAME, 1, User.Role.STUDENT)
        student.getCourseExecutions().add(courseExecution)
        courseExecution.getUsers().add(student)
        userRepository.save(student)

        def student2 = new User(USER_NAME2, USER_USERNAME2, 2, User.Role.STUDENT)
        student2.getCourseExecutions().add(courseExecution)
        courseExecution.getUsers().add(student2)
        userRepository.save(student2)

        when:
        tournamentService.addEnrolledStudentToTournament(student.getId(), tournament.getId())
        tournamentService.addEnrolledStudentToTournament(student2.getId(), tournament.getId())

        then: "students are enrolled in tournament"
        def result = tournamentRepository.findAll().get(0)
        result.getEnrolledStudents().size() == 2
        result.getEnrolledStudents().contains(student)
        result.getEnrolledStudents().contains(student2)
        student.getEnrolledTournaments().contains(result)
        student2.getEnrolledTournaments().contains(result)
    }

    def "teacher tries to enroll in tournament" () {
        given: "a teacher"
        def teacher = new User(USER_NAME, USER_USERNAME, 1, User.Role.TEACHER)
        teacher.getCourseExecutions().add(courseExecution)
        courseExecution.getUsers().add(teacher)
        userRepository.save(teacher)

        when:
        tournamentService.addEnrolledStudentToTournament(teacher.getId(), tournament.getId())

        then: "an exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.USER_NOT_STUDENT

    }

    @TestConfiguration
    static class TournamentServiceImplTestContextConfiguration {
        @Bean
        TournamentService tournamentService() {
            return new TournamentService()
        }
    }

}