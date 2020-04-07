package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.service

import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.Tournament

//-----------External Imports-----------
import spock.lang.Specification
import org.springframework.context.annotation.Bean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

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
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.repository.TournamentRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto.TournamentDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.TopicRepository

@DataJpaTest
class GetAvailableTournamentTest extends Specification {
    public static final String COURSE_NAME = "Software Architecture"
    public static final String TOURNAMENT_TITLE = "Tournament 1"
    public static final String ACRONYM = "AS1"
    public static final String ACADEMIC_TERM = "1 SEM"
    public static final String STUDENT_NAME = "Student 1"
    public static final String STUDENT_USERNAME = "std1"
    public static final String STUDENT_NAME2 = "Student 2"
    public static final String STUDENT_USERNAME2 = "std2"
    public static final String STUDENT_NAME3 = "Student 3"
    public static final String STUDENT_USERNAME3 = "std3"
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
    def tournament2
    def topic
    def student2
    def formatter
    def availableDate
    def conclusionDate
    def availableDate2
    def conclusionDate2

    def setup() {
        formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

        availableDate = LocalDateTime.now()
        conclusionDate = LocalDateTime.now().plusDays(1)

        availableDate2 = LocalDateTime.now().plusDays(1)
        conclusionDate2 = LocalDateTime.now().plusDays(2)

        course = new Course(COURSE_NAME, Course.Type.TECNICO)
        courseRepository.save(course)

        courseExecution = new CourseExecution(course, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO)
        courseExecutionRepository.save(courseExecution)

        student = new User(STUDENT_NAME, STUDENT_USERNAME, 1, User.Role.STUDENT)
        student.getCourseExecutions().add(courseExecution)
        courseExecution.getUsers().add(student)
        userRepository.save(student)

        student2 = new User(STUDENT_NAME2, STUDENT_USERNAME2, 2, User.Role.STUDENT)
        student2.getCourseExecutions().add(courseExecution)
        courseExecution.getUsers().add(student2)
        userRepository.save(student2)

        topic = new Topic()
        topic.setName(TOPIC_NAME)
        topicRepository.save(topic)

        tournament = new Tournament()
        tournament.setTitle(TOURNAMENT_TITLE)
        tournament.setStudent(student)
        tournament.addEnrolledStudent(student)
        tournament.setCourseExecution(courseExecution)
        tournament.setAvailableDate(availableDate)
        tournament.setConclusionDate(conclusionDate)
        courseExecution.addTournament(tournament)
        tournament.addTopic(topic)
        tournamentRepository.save(tournament)

        tournamentService.addEnrolledStudentToTournament(student.getId(), tournament.getId())
        tournamentService.addEnrolledStudentToTournament(student2.getId(), tournament.getId())

        tournament2 = new Tournament()
        tournament2.setTitle(TOURNAMENT_TITLE)
        tournament2.setStudent(student)
        tournament2.addEnrolledStudent(student)
        tournament2.setCourseExecution(courseExecution)
        tournament2.setAvailableDate(availableDate2)
        tournament2.setConclusionDate(conclusionDate2)
        courseExecution.addTournament(tournament2)
        tournament2.addTopic(topic)
        tournamentRepository.save(tournament2)

        tournamentService.addEnrolledStudentToTournament(student.getId(), tournament2.getId())
        tournamentService.addEnrolledStudentToTournament(student2.getId(), tournament2.getId())
    }

    def "get open tournament created by student"() {
        given: "the creator of the tournament"

        when:
        def tournaments = tournamentService.getOpenTournaments(student.getId(), courseExecution.getId())

        then:
        tournaments.size() == 2
        tournaments.get(0).getTitle() == tournament.getTitle()
        tournaments.get(1).getTitle() == tournament2.getTitle()
    }

    def "get open tournament not created by student"() {
        given: "a student"

        when:
        def tournaments = tournamentService.getOpenTournaments(student2.getId(), courseExecution.getId())

        then:
        tournaments.size() == 1
        tournaments.get(0).getTitle() == tournament.getTitle()
    }

    def "get tournament not yet opened, but created by student"() {
        given: "a student"

        when:
        def tournaments = tournamentService.getOpenTournaments(student.getId(), courseExecution.getId())

        then:
        tournaments.size() == 2
        tournaments.get(0).getTitle() == tournament.getTitle()
        tournaments.get(1).getTitle() == tournament2.getTitle()
    }

    def "get tournament not yet opened and not created by student"() {
        given: "a student"

        when:
        def tournaments = tournamentService.getOpenTournaments(student2.getId(), courseExecution.getId())

        then:
        tournaments.size() == 1
        tournaments.get(0).getTitle() == tournament.getTitle()
    }

    @TestConfiguration
    static class TournamentServiceImplTestContextConfiguration {
        @Bean
        TournamentService tournamentService() {
            return new TournamentService()
        }
    }
}