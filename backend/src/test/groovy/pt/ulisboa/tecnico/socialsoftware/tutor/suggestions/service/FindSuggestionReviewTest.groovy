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
import pt.ulisboa.tecnico.socialsoftware.tutor.suggestions.SuggestionReviewService
import pt.ulisboa.tecnico.socialsoftware.tutor.suggestions.SuggestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.suggestions.domain.Suggestion
import pt.ulisboa.tecnico.socialsoftware.tutor.suggestions.domain.SuggestionReview
import pt.ulisboa.tecnico.socialsoftware.tutor.suggestions.repository.SuggestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.suggestions.repository.SuggestionReviewRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification

@DataJpaTest
class FindSuggestionReviewTest extends Specification {
    public static final String COURSE_NAME = "Software Architecture"
    public static final String ACRONYM = "AS1"
    public static final String ACADEMIC_TERM = "1 SEM"
    public static final String QUESTION_TITLE = 'a question title'
    public static final String QUESTION_CONTENT = 'a question content'
    public static final String OPTION_CONTENT = "option id content"
    public static final String STUDENT_NAME = "Anonymous User"
    public static final String STUDENT_USERNAME = "anon"
    public static final String TEACHER_NAME = "Teacher"
    public static final String TEACHER_USERNAME = "teacher"
    public static final String SUGGESTION_REVIEW_JUSTIFICATION = "suggestion review justification"

    @Autowired
    CourseRepository courseRepository

    @Autowired
    CourseExecutionRepository courseExecutionRepository

    @Autowired
    QuestionRepository questionRepository

    @Autowired
    SuggestionRepository suggestionRepository

    @Autowired
    SuggestionReviewRepository suggestionReviewRepository

    @Autowired
    SuggestionReviewService suggestionReviewService

    @Autowired
    UserRepository userRepository

    def course
    def courseExecution
    def student
    def question
    def teacher
    def suggestion
    def suggestionReview

    def setup () {
        course = new Course(COURSE_NAME, Course.Type.TECNICO)
        courseRepository.save(course)

        courseExecution = new CourseExecution(course, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO)
        courseExecutionRepository.save(courseExecution)

        student = new User(STUDENT_NAME, STUDENT_USERNAME, 1, User.Role.STUDENT)
        student.getCourseExecutions().add(courseExecution)
        courseExecution.getUsers().add(student)
        userRepository.save(student)

        teacher = new User(TEACHER_NAME, TEACHER_USERNAME, 2, User.Role.TEACHER)
        teacher.getCourseExecutions().add(courseExecution)
        courseExecution.getUsers().add(teacher)
        userRepository.save(teacher)

        question = new Question()
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
        suggestion.setApproved(false)
        suggestion.setQuestion(question)
        suggestionRepository.save(suggestion)

        suggestionReview = new SuggestionReview()
        suggestionReview.setTeacher(teacher)
        suggestionReview.setSuggestion(suggestion)
        suggestionReview.setApproved(false)
        suggestionReview.setJustification(SUGGESTION_REVIEW_JUSTIFICATION)
        suggestionReviewRepository.save(suggestionReview)
    }

    def "find suggestion reviews for non-existing suggestion"() {
        when:
        suggestionReviewService.findSuggestionReviewsBySuggestion(100)

        then: "an exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.SUGGESTION_NOT_FOUND
    }

    def "find suggestion reviews for existing suggestion"() {
        when:
        def suggestionReviews = suggestionReviewService.findSuggestionReviewsBySuggestion(suggestion.getId())

        then: "one suggestion review is retrieved"
        suggestionReviews.size() == 1
        suggestionReviews.get(0).getCreationDate() == suggestionReview.getCreationDate()
        suggestionReviews.get(0).getId() == suggestionReview.getId()
        suggestionReviews.get(0).getJustification() == suggestionReview.getJustification()
        suggestionReviews.get(0).getApproved() == suggestionReview.getApproved()
    }

    def "find suggestion reviews for non-existing user"() {
        when:
        suggestionReviewService.findSuggestionReviewsByTeacher(100)

        then: "an exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.USER_NOT_FOUND
    }

    def "find suggestion reviews for existing user"() {
        when:
        def suggestionReviews = suggestionReviewService.findSuggestionReviewsByTeacher(teacher.getId())

        then: "one suggestion is retrieved"
        suggestionReviews.size() == 1
        suggestionReviews.get(0).getCreationDate() == suggestionReview.getCreationDate()
        suggestionReviews.get(0).getId() == suggestionReview.getId()
        suggestionReviews.get(0).getJustification() == suggestionReview.getJustification()
        suggestionReviews.get(0).getApproved() == suggestionReview.getApproved()
    }

    def "find suggestion review by non-existing id"() {
        when:
        suggestionReviewService.findSuggestionReviewById(100)

        then: "an exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.SUGGESTION_REVIEW_NOT_FOUND
    }

    def "find suggestion review by id"() {
        given: "a suggestion review"
        def id = suggestionReview.getId()
        def sr

        when:
        sr = suggestionReviewService.findSuggestionReviewById(id)

        then: "correct suggestion is fetched"
        sr.getCreationDate() == suggestionReview.getCreationDate()
        sr.getId() == suggestionReview.getId()
        sr.getJustification() == suggestionReview.getJustification()
        sr.getApproved() == suggestionReview.getApproved()
    }

    @TestConfiguration
    static class SuggestionReviewServiceImplTestContextConfiguration {

        @Bean
        SuggestionReviewService suggestionReviewService() {
            return new SuggestionReviewService()
        }
    }
}
