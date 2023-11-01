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
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.suggestions.SuggestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.suggestions.domain.Suggestion
import pt.ulisboa.tecnico.socialsoftware.tutor.suggestions.repository.SuggestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification

@DataJpaTest
class FindSuggestionTest extends Specification {
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
    def suggestion

    def setup() {
        course = new Course(COURSE_NAME, Course.Type.TECNICO)
        courseRepository.save(course)

        courseExecution = new CourseExecution(course, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO)
        courseExecutionRepository.save(courseExecution)

        student = new User(STUDENT_NAME, STUDENT_USERNAME, 1, User.Role.STUDENT)
        student.getCourseExecutions().add(courseExecution)
        courseExecution.getUsers().add(student)
        userRepository.save(student)

        def question = new Question()
        question.setKey(1)
        question.setTitle(QUESTION_TITLE)
        question.setContent(QUESTION_CONTENT)
        question.setStatus(Question.Status.AVAILABLE)
        question.setNumberOfAnswers(2)
        question.setNumberOfCorrect(1)
        question.setCourse(course)
        course.addQuestion(question)
        questionRepository.save(question)

        suggestion = new Suggestion()
        suggestion.setStudent(student)
        suggestion.setStatus(Suggestion.Status.PENDING)
        suggestion.setQuestion(question)
        suggestionRepository.save(suggestion)
    }

    def "find suggestions for non-existing course"() {
        when:
        suggestionService.findSuggestionsByCourse(20)

        then: "an exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.COURSE_NOT_FOUND
    }

    def "find suggestions for existing course"() {
        when:
        def suggestions = suggestionService.findSuggestionsByCourse(course.getId())

        then: "one suggestion is retrieved"
        suggestions.size() == 1
        suggestions.get(0).getCreationDate() == suggestion.getCreationDate()
        suggestions.get(0).getId() == suggestion.getId()
        suggestions.get(0).getStatus() == suggestion.getStatus().name()
    }

    def "find suggestions for non-existing user"() {
        when:
        suggestionService.findSuggestionsByStudent(20)

        then: "an exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.USER_NOT_FOUND
    }

    def "find suggestions for existing user"() {
        when:
        def suggestions = suggestionService.findSuggestionsByStudent(student.getId())

        then: "one suggestion is retrieved"
        suggestions.size() == 1
        suggestions.get(0).getCreationDate() == suggestion.getCreationDate()
        suggestions.get(0).getId() == suggestion.getId()
        suggestions.get(0).getStatus() == suggestion.getStatus().name()
    }

    def "find suggestion by id"() {
        given: "a suggestion"
        def id = suggestion.getId()
        def sug

        when:
        sug = suggestionService.findSuggestionById(id)

        then: "correct suggestion is fetched"
        sug.getId() == suggestion.getId()
        sug.getStatus() == suggestion.getStatus().name()
        sug.getCreationDate() == suggestion.getCreationDate()
    }

    def "find suggestion by non-existing id"() {
        when:
        suggestionService.findSuggestionById(20)

        then: "an exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.SUGGESTION_NOT_FOUND
    }

    @TestConfiguration
    static class SuggestionServiceImplTestContextConfiguration {

        @Bean
        SuggestionService suggestionService() {
            return new SuggestionService()
        }

        @Bean
        QuestionService questionService() {
            return new QuestionService()
        }
    }
}
