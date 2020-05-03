package pt.ulisboa.tecnico.socialsoftware.tutor.query.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuestionAnswerRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuizAnswerRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.query.QueryService
import pt.ulisboa.tecnico.socialsoftware.tutor.query.dto.QueryDto
import pt.ulisboa.tecnico.socialsoftware.tutor.query.repository.QueryRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizQuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification

@DataJpaTest
class GetQueryServiceSpockPerformanceTest extends Specification {
    public static final String COURSE_NAME = "Software Architecture"
    public static final String ACRONYM = "AS1"
    public static final String ACADEMIC_TERM = "1 SEM"
    public static final String QUESTION_TITLE = 'question title'
    public static final String QUESTION_CONTENT = 'question content'
    public static final String OPTION_CONTENT = "optionId content"
    public static final String USER_NAME = "Student Name"
    public static final String USER_USERNAME = "Student Username"
    public static final String QUERY_TITLE = 'query title'
    public static final String QUERY_CONTENT = 'query content'

    @Autowired
    QueryService queryService

    @Autowired
    CourseRepository courseRepository

    @Autowired
    CourseExecutionRepository courseExecutionRepository

    @Autowired
    QuestionRepository questionRepository

    @Autowired
    QuizQuestionRepository quizQuestionRepository

    @Autowired
    QuizAnswerRepository quizAnswerRepository

    @Autowired
    QuestionAnswerRepository questionAnswerRepository

    @Autowired
    UserRepository userRepository

    @Autowired
    QueryRepository queryRepository

    def course
    def courseExecution
    def question
    def student
    def quizQuestion

    def setup() {
        course = new Course(COURSE_NAME, Course.Type.TECNICO)
        courseRepository.save(course)

        courseExecution = new CourseExecution(course, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO)
        courseExecutionRepository.save(courseExecution)

        question = new Question()
        question.setKey(1)
        question.setCourse(course)
        question.setTitle(QUESTION_TITLE)
        question.setContent(QUESTION_CONTENT)
        question.setStatus(Question.Status.AVAILABLE)
        course.addQuestion(question)
        questionRepository.save(question)

        student = new User(USER_NAME, USER_USERNAME, 1, User.Role.STUDENT)
        student.getCourseExecutions().add(courseExecution)
        courseExecution.getUsers().add(student)
        userRepository.save(student)

        quizQuestion = new QuizQuestion()
        quizQuestion.setQuestion(question)
        question.addQuizQuestion(quizQuestion)
        quizQuestionRepository.save(quizQuestion)
    }

    def "create a 1000 queries and fetch them 10 000 times" () {
        given: "answer to the question by student"
        def quizAnswer = new QuizAnswer()
        quizAnswer.setUser(student)
        student.addQuizAnswer(quizAnswer)
        quizAnswerRepository.save(quizAnswer)

        def questionAnswer = new QuestionAnswer()
        questionAnswer.setQuizAnswer(quizAnswer)
        questionAnswer.setQuizQuestion(quizQuestion)
        quizAnswer.addQuestionAnswer(questionAnswer)
        quizQuestion.addQuestionAnswer(questionAnswer)
        questionAnswerRepository.save(questionAnswer)

        and: "1000 queries"
        1.upto(1, {
            and: "a queryDTO"
            def queryDTO = new QueryDto()
            queryDTO.setTitle(QUERY_TITLE)
            queryDTO.setContent(QUERY_CONTENT)
            queryService.createQuery(question.getId(), student.getId(), questionAnswer.getId(), queryDTO)
        })

        when:
        1.upto(1, {
            queryService.getQueriesToQuestion(question.getId())
        })

        then:
        true
    }

    def "create and share 1000 queries and fetch them 10 000 times" () {
        given: "answer to the question by student"
        def quizAnswer = new QuizAnswer()
        quizAnswer.setUser(student)
        student.addQuizAnswer(quizAnswer)
        quizAnswerRepository.save(quizAnswer)

        def questionAnswer = new QuestionAnswer()
        questionAnswer.setQuizAnswer(quizAnswer)
        questionAnswer.setQuizQuestion(quizQuestion)
        quizAnswer.addQuestionAnswer(questionAnswer)
        quizQuestion.addQuestionAnswer(questionAnswer)
        questionAnswerRepository.save(questionAnswer)

        and: "1000 queries"
        1.upto(1, {
            and: "a queryDTO"
            def queryDTO = new QueryDto()
            queryDTO.setTitle(QUERY_TITLE)
            queryDTO.setContent(QUERY_CONTENT)
            def result = queryService.createQuery(question.getId(), student.getId(), questionAnswer.getId(), queryDTO)
            queryService.shareQuery(result.getId())
        })

        when:
        1.upto(1, {
            queryService.getSharedQueries(question.getId())
        })

        then:
        true
    }

    @TestConfiguration
    static class QueryServiceImplTestContextConfiguration {

        @Bean
        QueryService queryService() {
            return new QueryService()
        }
    }
}

