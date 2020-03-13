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
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.StudentDto
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentService
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.Tournament
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.repository.TournamentRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto.TournamentDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.TopicRepository


@DataJpaTest
class RemoveTournamentTest extends Specification {
    public static final String COURSE_NAME = "Software Architecture"
    public static final String ACRONYM = "AS1"
    public static final String ACADEMIC_TERM = "1 SEM"
    public static final String STUDENT_NAME = "Student 1"
    public static final String STUDENT_USERNAME = "std1"
    public static final String STUDENT_NAME2 = "Student 2"
    public static final String STUDENT_USERNAME2 = "std2"
    public static final String TOPIC_NAME = "Topic"

    @Autowired
    CourseRepository courseRepository

    @Autowired
    CourseExecutionRepository courseExecutionRepository

    @Autowired
    TopicRepository topicRepository

    @Autowired
    TournamentRepository tournamentRepository

    @Autowired
    TournamentService tournamentService

    @Autowired
    UserRepository userRepository

    def course
    def courseExecution
    def student
    def tournament
    def topic

    def setup() {

        course = new Course(COURSE_NAME, Course.Type.TECNICO)
        courseRepository.save(course)

        courseExecution = new CourseExecution(course, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO)
        courseExecutionRepository.save(courseExecution)

        student = new User(STUDENT_NAME, STUDENT_USERNAME, 1, User.Role.STUDENT)
        student.getCourseExecutions().add(courseExecution)
        courseExecution.getUsers().add(student)
        userRepository.save(student)

        topic = new Topic()
        topic.setName(TOPIC_NAME)
        topicRepository.save(topic)

        tournament = new Tournament()
        tournament.setKey(1)
        tournament.setStudent(student)
        tournament.addEnrolledStudent(student)
        tournament.setCourseExecution(courseExecution)
        courseExecution.addTournament(tournament)
        tournament.addTopic(topic)
        tournamentRepository.save(tournament)
    }

    def "tournament creator removes a tournament"() {
        given: "the creator of a tournament"

        when:
        tournamentService.removeTournament(student.getId(), tournament.getId())

        then: "the tournament is removed"
        tournamentRepository.count() == 0L
        student.getCreatedTournaments().isEmpty()
        tournament.getStudent() == null
        student.getEnrolledTournaments().isEmpty()
        !tournament.getEnrolledStudents().contains(student)
        !topic.getTournaments().contains(tournament)
        !tournament.getTopics().contains(topic)
    }

    def "student tries to remove a tournament not created by him"() {
        given: "a second student"
        def student2 = new User(STUDENT_NAME2, STUDENT_USERNAME2, 2, User.Role.STUDENT)
        student2.getCourseExecutions().add(courseExecution)
        courseExecution.getUsers().add(student2)
        userRepository.save(student2)
        tournamentService.addEnrolledStudentToTournament(student2.getId(), tournament.getId())

        when:
        tournamentService.removeTournament(student2.getId(), tournament.getId())

        then: "an exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.STUDENT_NOT_CREATOR
    }

    @TestConfiguration
    static class TournamentServiceImplTestContextConfiguration {
        @Bean
        TournamentService tournamentService() {
            return new TournamentService()
        }
    }
}