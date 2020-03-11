package pt.ulisboa.tecnico.socialsoftware.tutor.suggestions.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.suggestions.SuggestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.suggestions.dto.SuggestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.suggestions.repository.SuggestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.StudentDto
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto
import spock.lang.Specification

@DataJpaTest
class CreateSuggestionTest extends Specification {
    public static final String COURSE_NAME = "Software Architecture"
    public static final String ACRONYM = "AS1"
    public static final String ACADEMIC_TERM = "1 SEM"
    public static final String QUESTION_TITLE = 'a question title'
    public static final String QUESTION_CONTENT = 'a question content'
    public static final String OPTION_CONTENT = "option id content"
    public static final String STUDENT_NAME = "Anonymous User"
    public static final String STUDENT_USERNAME = "anon"

    @Autowired
    CourseRepository courseRepository

    @Autowired
    CourseExecutionRepository courseExecutionRepository

    @Autowired
    QuestionRepository questionRepository

    @Autowired
    SuggestionRepository suggestionRepository

    @Autowired
    SuggestionService suggestionService

    @Autowired
    UserRepository userRepository

    def course
    def courseExecution
    def student

    def setup () {
        course = new Course(COURSE_NAME, Course.Type.TECNICO)
        courseRepository.save(course)

        courseExecution = new CourseExecution(course, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO)
        courseExecutionRepository.save(courseExecution)

        student = new User(STUDENT_NAME, STUDENT_USERNAME, 1, User.Role.STUDENT)
        student.getCourseExecutions().add(courseExecution)
        courseExecution.getUsers().add(student)
        userRepository.save(student)
    }

    def "create suggestion with user and question"() {
        given: "a questionDto"
        def questionDto = new QuestionDto()
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_TITLE)
        questionDto.setContent(QUESTION_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())

        and: "a suggestionDto"
        def suggestionDto = new SuggestionDto()
        suggestionDto.setKey(1)
        suggestionDto.setStudent(new UserDto(student))
        suggestionDto.setApproved(false)
        suggestionDto.setQuestion(questionDto)

        when:
        suggestionService.createSuggestion(suggestionDto)

        then: "the correct suggestion is inside the repository"
        suggestionRepository.count() == 1L
        def result = suggestionRepository.findAll().get(0)
        result.getId() != null
        result.getKey() == 1
        result.getQuestion().getTitle() == QUESTION_TITLE
        result.getQuestion().getContent() == QUESTION_CONTENT
        result.getStudent().getName() == STUDENT_NAME
        result.getStudent().getUsername() == STUDENT_USERNAME
    }

    def "create suggestion without user" () {
        given: "a questionDto"
        def questionDto = new QuestionDto()
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_TITLE)
        questionDto.setContent(QUESTION_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())

        and: "a suggestionDto"
        def suggestionDto = new SuggestionDto()
        suggestionDto.setKey(1)
        suggestionDto.setApproved(false)
        suggestionDto.setQuestion(questionDto)

        when:
        suggestionService.createSuggestion(suggestionDto)

        then: "an exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.SUGGESTION_MUST_HAVE_USER
    }

    def "create suggestion without question"() {
        given: "a questionDto"
        def questionDto = new QuestionDto()
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_TITLE)
        questionDto.setContent(QUESTION_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())

        and: "a suggestionDto"
        def suggestionDto = new SuggestionDto()
        suggestionDto.setKey(1)
        suggestionDto.setApproved(false)
        suggestionDto.setStudent(new UserDto(student))

        when:
        suggestionService.createSuggestion(suggestionDto)

        then: "an exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.SUGGESTION_MUST_HAVE_QUESTION
    }

    @TestConfiguration
    static class SuggestionServiceImplTestContextConfiguration {

        @Bean
        SuggestionService suggestionService() {
            return new SuggestionService()
        }
    }
}
