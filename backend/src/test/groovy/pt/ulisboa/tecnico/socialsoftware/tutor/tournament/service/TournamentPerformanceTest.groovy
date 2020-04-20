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
class TournamentPerformanceTest extends Specification {
    public static final String COURSE_NAME = "Software Architecture"
    public static final String TOURNAMENT_TITLE = "Tournament 1"
    public static final String ACRONYM = "AS1"
    public static final String ACADEMIC_TERM = "1 SEM"
    public static final String STUDENT_NAME = "Student 1"
    public static final String STUDENT_USERNAME = "std1"
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
    def student2
    def formatter
    def availableDate
    def conclusionDate

    def setup() {
        formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

        availableDate = LocalDateTime.now()
        conclusionDate = LocalDateTime.now().plusDays(1)

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

    }

    def "create and get tournaments"() {
        given: "1000 tournaments"
        1.upto(1, {
            def topicDto = new TopicDto(topic)
            def tournamentDto = new TournamentDto()
            tournamentDto.setTitle(TOURNAMENT_TITLE)
            tournamentDto.setNumberQuestions(2)
            tournamentDto.setAvailableDate(availableDate.format(formatter))
            tournamentDto.setConclusionDate(conclusionDate.format(formatter))
            tournamentDto.addTopic(topicDto)

            tournamentService.createTournament(courseExecution.getId(), student.getId(), tournamentDto)
        })
        when:
        1.upto(1, {
            tournamentService.getOpenTournaments(student.getId(), courseExecution.getId())
        })

        then:
        true

    }

    @TestConfiguration
    static class TournamentServiceImplTestContextConfiguration {
        @Bean
        TournamentService tournamentService() {
            return new TournamentService()
        }
    }
}