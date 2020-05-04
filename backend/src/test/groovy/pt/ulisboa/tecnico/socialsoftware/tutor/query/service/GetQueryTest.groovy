package pt.ulisboa.tecnico.socialsoftware.tutor.query.service

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
import pt.ulisboa.tecnico.socialsoftware.tutor.query.QueryService
import pt.ulisboa.tecnico.socialsoftware.tutor.query.domain.Query
import pt.ulisboa.tecnico.socialsoftware.tutor.query.repository.AnswerQueryRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.query.repository.QueryRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification

@DataJpaTest
class GetQueryTest extends Specification {
    public static final String COURSE_NAME = "Software Architecture"
    public static final String ACRONYM = "AS1"
    public static final String ACADEMIC_TERM = "1 SEM"
    public static final String QUESTION_TITLE = 'question title'
    public static final String QUESTION_CONTENT = 'question content'
    public static final String OPTION_CONTENT = "optionId content"
    public static final String STUDENT_NAME = "Student Name"
    public static final String STUDENT_USERNAME = "Student Username"
    public static final String TEACHER_NAME = "Teacher Name"
    public static final String TEACHER_USERNAME = "Teacher Username"
    public static final String QUERY_TITLE = 'query title'
    public static final String QUERY_CONTENT = 'query content'
    public static final Integer QUERY_INVALID_ID = 2048
    public static final Integer QUESTION_INVALID_ID = 1024
    public static final Integer USER_INVALID_ID = 256

    @Autowired
    QueryService queryService

    @Autowired
    CourseRepository courseRepository

    @Autowired
    CourseExecutionRepository courseExecutionRepository

    @Autowired
    QuestionRepository questionRepository

    @Autowired
    UserRepository userRepository

    @Autowired
    QueryRepository queryRepository

    @Autowired
    AnswerQueryRepository answerQueryRepository

    def course
    def courseExecution
    def question
    def student
    def query
    def teacher

    def setup() {
        course = new Course(COURSE_NAME, Course.Type.TECNICO)
        courseRepository.save(course)

        courseExecution = new CourseExecution(course, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO)
        courseExecution.setCourse(course)
        course.addCourseExecution(courseExecution)
        courseExecutionRepository.save(courseExecution)

        question = new Question()
        question.setKey(1)
        question.setCourse(course)
        question.setTitle(QUESTION_TITLE)
        question.setContent(QUESTION_CONTENT)
        question.setStatus(Question.Status.AVAILABLE)
        course.addQuestion(question)
        questionRepository.save(question)

        student = new User(STUDENT_NAME, STUDENT_USERNAME, 1, User.Role.STUDENT)
        student.getCourseExecutions().add(courseExecution)
        courseExecution.getUsers().add(student)
        userRepository.save(student)

        teacher = new User(TEACHER_NAME, TEACHER_USERNAME, 2, User.Role.TEACHER)
        teacher.getCourseExecutions().add(courseExecution)
        courseExecution.getUsers().add(teacher)
        userRepository.save(teacher)

        query = new Query()
        query.setTitle(QUERY_TITLE)
        query.setContent(QUERY_CONTENT)
        query.setQuestion(question)
        query.setShared(false)
        question.addQuery(query)
        query.setStudent(student)
        student.addQuery(query)
        queryRepository.save(query)
    }

    def 'get queries by Id'() {
        when:
        def queryDto = queryService.findQueryById(query.getId())

        then: 'the query is retrieved'
        queryRepository.count() == 1L
        def result = queryRepository.findAll().get(0)
        result.getStudent() == student
        student.getQueries().size() == 1
        result.getQuestion() == question
        question.getQueries().size() == 1
        !result.getShared()
        and: 'the return statement contains one query'
        queryDto.getTitle() == query.getTitle()
        queryDto.getContent() == query.getContent()
        queryDto.getId() == result.getId()
        queryDto.getShared() == result.getShared()
    }

    def 'get queries to a question'() {
        when:
        def queryDtos = queryService.getQueriesToQuestion(question.getId())

        then: 'the query is retrieved'
        queryRepository.count() == 1L
        def result = queryRepository.findAll().get(0)
        result.getStudent() == student
        student.getQueries().size() == 1
        result.getQuestion() == question
        question.getQueries().size() == 1
        !result.getShared()
        and: 'the return statement contains one query'
        queryDtos.size() == 1
        def queryResult = queryDtos.get(0)
        queryResult.getTitle() == query.getTitle()
        queryResult.getContent() == query.getContent()
        queryResult.getId() == result.getId()
        queryResult.getShared() == result.getShared()
    }

    def 'get queries by a student'() {
        when:
        def queryDtos = queryService.getQueriesByStudent(student.getId())

        then: 'the query is retrieved'
        queryRepository.count() == 1L
        def result = queryRepository.findAll().get(0)
        result.getStudent() == student
        student.getQueries().size() == 1
        result.getQuestion() == question
        question.getQueries().size() == 1
        !result.getShared()
        and: 'the return statement contains one query'
        queryDtos.size() == 1
        def queryResult = queryDtos.get(0)
        queryResult.getTitle() == query.getTitle()
        queryResult.getContent() == query.getContent()
        queryResult.getId() == result.getId()
        queryResult.getShared() == result.getShared()
    }

    def 'get queries in teacher courses'() {
        when:
        def queryDtos = queryService.getQueriesInCourse(teacher.getId())

        then: 'the query is retrieved'
        queryRepository.count() == 1L
        def result = queryRepository.findAll().get(0)
        result.getStudent() == student
        student.getQueries().size() == 1
        result.getQuestion() == question
        question.getQueries().size() == 1
        !result.getShared()
        and: 'the return statement contains one query'
        queryDtos.size() == 1
        def queryResult = queryDtos.get(0)
        queryResult.getTitle() == query.getTitle()
        queryResult.getContent() == query.getContent()
        queryResult.getId() == result.getId()
        queryResult.getShared() == result.getShared()
    }

    def 'get shared queries to question'() {
        given: 'a shared query'
        query.share()

        when:
        def queryDtos = queryService.getSharedQueries(question.getId())

        then: 'the query is retrieved'
        queryRepository.count() == 1L
        def result = queryRepository.findAll().get(0)
        result.getStudent() == student
        student.getQueries().size() == 1
        result.getQuestion() == question
        question.getQueries().size() == 1
        result.getShared()
        and: 'the return statement contains one query'
        queryDtos.size() == 1
        def queryResult = queryDtos.get(0)
        queryResult.getTitle() == query.getTitle()
        queryResult.getContent() == query.getContent()
        queryResult.getId() == result.getId()
        queryResult.getShared() == result.getShared()

    }

    def 'get query by id with invalid id'() {
        when:
        def queryDto = queryService.findQueryById(QUERY_INVALID_ID)

        then: 'exception user not found'
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.QUERY_NOT_FOUND
    }

    def 'get queries by user with invalid id'() {
        when:
        def queryDtos = queryService.getQueriesByStudent(USER_INVALID_ID)

        then: 'exception user not found'
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.USER_NOT_FOUND
    }

    def 'get queries by question with invalid id'() {
        when:
        def queryDtos = queryService.getQueriesToQuestion(QUESTION_INVALID_ID)

        then: 'exception question not found'
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.QUESTION_NOT_FOUND
    }

    def 'get queries in teacher courses with invalid id'() {
        when:
        def queryDtos = queryService.getQueriesInCourse(USER_INVALID_ID)

        then: 'exception user not found'
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.USER_NOT_FOUND
    }

    @TestConfiguration
    static class QueryServiceImplTestContextConfiguration {

        @Bean
        QueryService queryService() {
            return new QueryService()
        }
    }
}
