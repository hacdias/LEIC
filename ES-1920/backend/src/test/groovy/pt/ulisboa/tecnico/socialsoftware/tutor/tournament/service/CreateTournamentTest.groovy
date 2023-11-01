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
class CreateTournamentTest extends Specification {
    public static final String COURSE_NAME = "Software Architecture"
    public static final String ACRONYM = "AS1"
    public static final String ACADEMIC_TERM = "1 SEM"
    public static final String TOPIC_NAME = 'TOPIC 1'
    public static final String TOPIC_NAME2 = 'TOPIC 2'
    public static final String USER_NAME = "User"
    public static final String USER_USERNAME = "Username"
    public static final String TOURNAMENT_TITLE = "TOURNAMENT 1"
    public static final String AVAILABLE_DATE = "2021-03-01 10:00"
    public static final String CONCLUSION_DATE = "2021-06-02 11:00"

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
    def topic2
    def topicDto
    def topicDto2
    
    def setup() {
        formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

        course = new Course(COURSE_NAME, Course.Type.TECNICO)
        courseRepository.save(course)

        courseExecution = new CourseExecution(course, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO)
        courseExecutionRepository.save(courseExecution)
        
        topic = new Topic()
        topic.setName(TOPIC_NAME)
        topicRepository.save(topic)

        topicDto = new TopicDto(topic)

        topic2 = new Topic()
        topic2.setName(TOPIC_NAME2)
        topicRepository.save(topic2)
               
        topicDto2 = new TopicDto(topic2)

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
        tournamentDto.setTitle(TOURNAMENT_TITLE)
        tournamentDto.setNumberQuestions(1)
        tournamentDto.setAvailableDate(availableDate.format(formatter))
        tournamentDto.setConclusionDate(conclusionDate.format(formatter))
        tournamentDto.addTopic(topicDto)
        tournamentDto.setStatus(Tournament.TournamentStatus.CAN_NOT_GENERATE_QUIZ.toString())
        
        when:
        tournamentService.createTournament(courseExecution.getId(), student.getId(), tournamentDto)

        then:
        tournamentRepository.count() == 1L
        def result = tournamentRepository.findAll().get(0)
        result.getId() != null
        result.getStudent().getId() == student.getId()
        result.getStudent().getName() == USER_NAME
        result.getStudent().getUsername() == USER_USERNAME
        result.getCreationDate() != null
        result.getAvailableDate().format(formatter) == availableDate.format(formatter)
        result.getConclusionDate().format(formatter) == conclusionDate.format(formatter)
        result.getTitle() == TOURNAMENT_TITLE
        result.getNumberQuestions() == 1
        result.getTopics().contains(topic)
        topicRepository.findAll().get(0).getTournaments().contains(result)
        student.getCreatedTournaments().contains(result)
        courseExecution.getTournaments().contains(result)
    }

    def "student creates a tournament with two topics and two questions"() {
        given: "a student"
        def student = new User(USER_NAME, USER_USERNAME, 1, User.Role.STUDENT)
        student.getCourseExecutions().add(courseExecution)
        courseExecution.getUsers().add(student)
        userRepository.save(student)    
        
        and: "a tournamentDto"
        def tournamentDto = new TournamentDto()
        tournamentDto.setTitle(TOURNAMENT_TITLE)
        tournamentDto.setNumberQuestions(2)
        tournamentDto.setAvailableDate(availableDate.format(formatter))
        tournamentDto.setConclusionDate(conclusionDate.format(formatter))
        tournamentDto.addTopic(topicDto)
        tournamentDto.addTopic(topicDto2)
        tournamentDto.setStatus(Tournament.TournamentStatus.CAN_NOT_GENERATE_QUIZ.toString())
        
        when:
        tournamentService.createTournament(courseExecution.getId(), student.getId(), tournamentDto)

        then:
        tournamentRepository.count() == 1L
        def result = tournamentRepository.findAll().get(0)
        result.getId() != null
        result.getStudent().getName() == USER_NAME
        result.getStudent().getUsername() == USER_USERNAME
        result.getCreationDate() != null
        result.getAvailableDate().format(formatter) == availableDate.format(formatter)
        result.getConclusionDate().format(formatter) == conclusionDate.format(formatter)
        result.getTitle() == TOURNAMENT_TITLE
        result.getNumberQuestions() == 2
        result.getTopics().contains(topic)
        result.getTopics().contains(topic2)
        topicRepository.count() == 2L
        topicRepository.findAll().get(0).getTournaments().contains(result)
        topicRepository.findAll().get(1).getTournaments().contains(result)
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
        tournamentDto.setTitle(TOURNAMENT_TITLE)
        tournamentDto.setNumberQuestions(1)
        tournamentDto.setAvailableDate(availableDate.format(formatter))
        tournamentDto.setConclusionDate(conclusionDate.format(formatter))
        tournamentDto.addTopic(topicDto)
        tournamentDto.setStatus(Tournament.TournamentStatus.CAN_NOT_GENERATE_QUIZ.toString())

        when:
        tournamentService.createTournament(courseExecution.getId(), teacher.getId(), tournamentDto)

        then: "an exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.USER_NOT_STUDENT
    }

    def "student creates two tournaments"() {
        given: "a student"
        def student = new User(USER_NAME, USER_USERNAME, 1, User.Role.STUDENT)
        student.getCourseExecutions().add(courseExecution)
        courseExecution.getUsers().add(student)
        userRepository.save(student)      

        and: "two tournamentDtos"
        def tournamentDto = new TournamentDto()
        tournamentDto.setTitle(TOURNAMENT_TITLE)
        tournamentDto.setNumberQuestions(1)
        tournamentDto.setAvailableDate(availableDate.format(formatter))
        tournamentDto.setConclusionDate(conclusionDate.format(formatter))
        tournamentDto.addTopic(topicDto)
        tournamentDto.setStatus(Tournament.TournamentStatus.CAN_NOT_GENERATE_QUIZ.toString())
        
        def tournamentDto2 = new TournamentDto()
        tournamentDto2.setTitle(TOURNAMENT_TITLE)
        tournamentDto2.setNumberQuestions(1)
        tournamentDto2.setAvailableDate(availableDate.format(formatter))
        tournamentDto2.setConclusionDate(conclusionDate.format(formatter))
        tournamentDto2.addTopic(topicDto)
        tournamentDto.setStatus(Tournament.TournamentStatus.CAN_NOT_GENERATE_QUIZ.toString())

        when:
        tournamentService.createTournament(courseExecution.getId(), student.getId(), tournamentDto)
        tournamentService.createTournament(courseExecution.getId(), student.getId(), tournamentDto2)

        then:
        tournamentRepository.count() == 2L
        def result = tournamentRepository.findAll().get(0)
        def result1 = tournamentRepository.findAll().get(1)
        result.getStudent().getId() == student.getId()
        result1.getStudent().getId() == student.getId()
        result.getTopics().contains(topic)
        topicRepository.findAll().get(0).getTournaments().contains(result)
        student.getCreatedTournaments().contains(result)
        courseExecution.getTournaments().contains(result)
        topicRepository.findAll().get(0).getTournaments().contains(result1)
        student.getCreatedTournaments().contains(result1)
        courseExecution.getTournaments().contains(result1)
    }

    def "invalid inputs: availableDate=#available_Date | conclusionDate=#conclusion_Date | title=#title | numberQuestions=#n || errorMessage=#errorMessage"() {
        given: "a student"
        def student = new User(USER_NAME, USER_USERNAME, 1, User.Role.STUDENT)
        student.getCourseExecutions().add(courseExecution)
        courseExecution.getUsers().add(student)
        userRepository.save(student)    

        and: "a topicDto"
        def topicDto = new TopicDto(topicRepository.findAll().get(0))   

        and: "a tournamentDto"
        def tournamentDto = new TournamentDto()
        tournamentDto.setTitle(title)
        tournamentDto.setNumberQuestions(n)
        tournamentDto.setAvailableDate(available_Date)
        tournamentDto.setConclusionDate(conclusion_Date)
        tournamentDto.addTopic(topicDto)
        tournamentDto.setStatus(Tournament.TournamentStatus.CAN_NOT_GENERATE_QUIZ.toString())

        when:
        tournamentService.createTournament(courseExecution.getId(), student.getId(), tournamentDto)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == errorMessage

        where:  
        available_Date    | conclusion_Date    | n   | title               || errorMessage
        null              | CONCLUSION_DATE    | 1   | TOURNAMENT_TITLE    || ErrorMessage.TOURNAMENT_NOT_CONSISTENT
        AVAILABLE_DATE    | null               | 1   | TOURNAMENT_TITLE    || ErrorMessage.TOURNAMENT_NOT_CONSISTENT
        AVAILABLE_DATE    | CONCLUSION_DATE    | 0   | TOURNAMENT_TITLE    || ErrorMessage.TOURNAMENT_NOT_CONSISTENT
        AVAILABLE_DATE    | CONCLUSION_DATE    | 1   | "   "               || ErrorMessage.TOURNAMENT_NOT_CONSISTENT
    }

    def "invalid input: no topic selected"() {
        given: "a student"
        def student = new User(USER_NAME, USER_USERNAME, 1, User.Role.STUDENT)
        student.getCourseExecutions().add(courseExecution)
        courseExecution.getUsers().add(student)
        userRepository.save(student)    

        and: "a tournamentDto"
        def tournamentDto = new TournamentDto()
        tournamentDto.setTitle(TOURNAMENT_TITLE)
        tournamentDto.setNumberQuestions(1)
        tournamentDto.setAvailableDate(availableDate.format(formatter))
        tournamentDto.setConclusionDate(conclusionDate.format(formatter))
        tournamentDto.setStatus(Tournament.TournamentStatus.CAN_NOT_GENERATE_QUIZ.toString())

        when:
        tournamentService.createTournament(courseExecution.getId(), student.getId(), tournamentDto)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.TOPIC_NOT_SELECTED
    }

    @TestConfiguration
    static class TournamentServiceImplTestContextConfiguration {
        @Bean
        TournamentService tournamentService() {
            return new TournamentService()
        }
    }
}

