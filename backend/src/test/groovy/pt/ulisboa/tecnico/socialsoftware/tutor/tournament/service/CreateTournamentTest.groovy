package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.service

//-----------External Imports-----------
import spock.lang.Specification
import org.springframework.context.annotation.Bean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


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
public class CreateTournamentTest extends Specification {
    public static final String COURSE_NAME = "Software Architecture"
    public static final String ACRONYM = "AS1"
    public static final String ACADEMIC_TERM = "1 SEM"
    public static final String TOPIC_NAME = 'TOPIC 1'
    public static final String USER_NAME = "User"
    public static final String USER_USERNAME = "Username"
    public static final String TOURNAMENT_TITLE = "TOURNAMENT 1"

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
    def creationDate
    def availableDate
    def conclusionDate
    def formatter
    def topic

    def setup() {
        formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

        course = new Course(COURSE_NAME, Course.Type.TECNICO)
        courseRepository.save(course)

        courseExecution = new CourseExecution(course, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO)
        courseExecutionRepository.save(courseExecution)
        
        topic = new Topic()
        topic.setName(TOPIC_NAME)
        topicRepository.save(topic)

        creationDate = LocalDateTime.now()
        availableDate = LocalDateTime.now()
        conclusionDate = LocalDateTime.now().plusDays(1)
    }

    def "student creates a tournament with one topic and one question"() {
        given: "a student"
        def student = new User(USER_NAME, USER_USERNAME, 1, User.Role.STUDENT)
        student.getCourseExecutions().add(courseExecution)
        courseExecution.getUsers().add(student)
        userRepository.save(student)    

        and: "a tournamentDto"
        def tournamentDto = new TournamentDto()
        tournamentDto.setKey(1)
        tournamentDto.setStudent(student)
        tournamentDto.setTitle(TOURNAMENT_TITLE)
        tournamentDto.setNumberQuestions(1)
        tournamentDto.setAvailableDate(availableDate.format(formatter))
        tournamentDto.setConclusionDate(conclusionDate.format(formatter))
        tournamentDto.addTopic(topic)
        
        when:
        tournamentService.createTournament(courseExecution.getId(), student.getId(), tournamentDto)

        then:
        tournamentRepository.count() == 1L
        def result = tournamentRepository.findAll().get(0)
        result.getId() != null
        result.getKey() == 1
        result.getStudent().getName() == USER_NAME
        result.getStudent().getUsername() == USER_USERNAME
        result.getCreationDate() != null
        result.getAvailableDate().format(formatter) == availableDate.format(formatter)
        result.getConclusionDate().format(formatter) == conclusionDate.format(formatter)
        result.getTitle() == TOURNAMENT_TITLE
        result.getNumberQuestions() == 1
        result.getTopics().contains(topic)
        topic.getTournaments().contains(result)
        student.getCreatedTournaments().contains(result)
        courseExecution.getTournaments().contains(result)
    }

    def "teacher tries to create tournament"() {
        given: "a teacher"
        def teacher = new User(USER_NAME, USER_USERNAME, 1, User.Role.TEACHER)
        teacher.getCourseExecutions().add(courseExecution)
        courseExecution.getUsers().add(teacher)
        userRepository.save(teacher)    

        and: "a tournamentDto"
        def tournamentDto = new TournamentDto()
        tournamentDto.setKey(1)
        tournamentDto.setStudent(teacher)
        tournamentDto.setTitle(TOURNAMENT_TITLE)
        tournamentDto.setNumberQuestions(1)
        tournamentDto.setAvailableDate(availableDate.format(formatter))
        tournamentDto.setConclusionDate(conclusionDate.format(formatter))
        tournamentDto.addTopic(topic)

        when:
        tournamentService.createTournament(courseExecution.getId(), teacher.getId(), tournamentDto)

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

