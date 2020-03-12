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
import pt.ulisboa.tecnico.socialsoftware.tutor.query.AnswerQueryService
import pt.ulisboa.tecnico.socialsoftware.tutor.query.domain.Query
import pt.ulisboa.tecnico.socialsoftware.tutor.query.dto.AnswerQueryDto
import pt.ulisboa.tecnico.socialsoftware.tutor.query.repository.AnswerQueryRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.query.repository.QueryRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification

@DataJpaTest
class CreateAnswerTest extends Specification {

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
    public static final String ANSWER_QUERY_CONTENT = 'answer query content'
    public static final String ANSWER_QUERY_CONTENT_2 = 'answer query content 2'
    public static final String USER_NAME_2 = "User 2 Name"
    public static final String USER_USERNAME_2 = "User 2 Username"

    @Autowired
    AnswerQueryService answerQueryService

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

        student = new User(STUDENT_NAME, STUDENT_USERNAME, 1, User.Role.STUDENT)
        student.getCourseExecutions().add(courseExecution)
        courseExecution.getUsers().add(student)
        userRepository.save(student)

        query = new Query()
        query.setKey(1)
        query.setTitle(QUERY_TITLE)
        query.setContent(QUERY_CONTENT)
        query.setQuestion(question)
        question.addQuery(query)
        query.setStudent(student)
        student.addQuery(query)
        queryRepository.save(query)
    }

    def "create an answer to a query"() {
        given: "a teacher who teaches the course"
        def teacher = new User(TEACHER_NAME, TEACHER_USERNAME, 2, User.Role.TEACHER)
        teacher.getCourseExecutions().add(courseExecution)
        courseExecution.getUsers().add(teacher)
        userRepository.save(teacher)

        and: "a answerQueryDTO"
        def answerQueryDTO = new AnswerQueryDto()
        answerQueryDTO.setContent(ANSWER_QUERY_CONTENT)
        answerQueryDTO.setKey()

        when:
        answerQueryService.createAnswerQuery(query.getId(), teacher.getId(), answerQueryDTO)

        then: "the correct answer query is inside the repository"
        answerQueryRepository.count() == 1L
        def result = answerQueryRepository.findAll().get(0)
        result.getId() != null
        result.getKey() == 1
        result.getContent() == ANSWER_QUERY_CONTENT
        result.getQuery().getTitle() == QUERY_TITLE
        result.getQuery().getContent() == QUERY_CONTENT
        result.getTeacher().getName() == TEACHER_NAME
        result.getTeacher().getUsername() == TEACHER_USERNAME
        query.getAnswers().contains(result)
        teacher.getQueryAnswers().contains(result)
    }

    def "create two answers to a query"() {
        given: "a teacher who teaches the course"
        def teacher = new User(TEACHER_NAME, TEACHER_USERNAME, 2, User.Role.TEACHER)
        teacher.getCourseExecutions().add(courseExecution)
        courseExecution.getUsers().add(teacher)
        userRepository.save(teacher)

        and: "a answerQueryDTO"
        def answerQueryDTO = new AnswerQueryDto()
        answerQueryDTO.setContent(ANSWER_QUERY_CONTENT)
        answerQueryDTO.setKey()

        and: "another answerQueryDTO"
        def answerQueryDTO2 = new AnswerQueryDto()
        answerQueryDTO2.setContent(ANSWER_QUERY_CONTENT_2)
        answerQueryDTO2.setKey()

        when:
        answerQueryService.createAnswerQuery(query.getId(), teacher.getId(), answerQueryDTO)
        answerQueryService.createAnswerQuery(query.getId(), teacher.getId(), answerQueryDTO2)

        then: "the correct answer queries are inside the repository"
        answerQueryRepository.count() == 2L
        def result = answerQueryRepository.findAll().get(0)
        result.getId() != null
        result.getKey() == 1
        result.getContent() == ANSWER_QUERY_CONTENT
        result.getQuery().getTitle() == QUERY_TITLE
        result.getQuery().getContent() == QUERY_CONTENT
        result.getTeacher().getName() == TEACHER_NAME
        result.getTeacher().getUsername() == TEACHER_USERNAME
        query.getAnswers().contains(result)
        teacher.getQueryAnswers().contains(result)

        def result2 = answerQueryRepository.findAll().get(1)
        result2.getId() != null
        result2.getKey() == 2
        result2.getContent() == ANSWER_QUERY_CONTENT_2
        result2.getQuery().getTitle() == QUERY_TITLE
        result2.getQuery().getContent() == QUERY_CONTENT
        result2.getTeacher().getName() == TEACHER_NAME
        result2.getTeacher().getUsername() == TEACHER_USERNAME
        query.getAnswers().contains(result2)
        teacher.getQueryAnswers().contains(result2)
    }

    def "not a teacher answers a query"() {
        given: "user not teacher"
        def user2 = new User(USER_NAME_2, USER_USERNAME_2, 2, User.Role.STUDENT)
        user2.getCourseExecutions().add(courseExecution)
        courseExecution.getUsers().add(user2)
        userRepository.save(user2)

        and: "a answerQueryDTO"
        def answerQueryDTO = new AnswerQueryDto()
        answerQueryDTO.setContent(ANSWER_QUERY_CONTENT)

        when:
        answerQueryService.createAnswerQuery(query.getId(), user2.getId(), answerQueryDTO)

        then: "exception User not a teacher"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.USER_NOT_TEACHER
    }

    def "teacher doesn't teach course"() {
        given: "a teacher who doesn't teach the course"
        def teacher = new User(TEACHER_NAME, TEACHER_USERNAME, 2, User.Role.TEACHER)
        userRepository.save(teacher)

        and: "a answerQueryDTO"
        def answerQueryDTO = new AnswerQueryDto()
        answerQueryDTO.setContent(ANSWER_QUERY_CONTENT)

        when:
        answerQueryService.createAnswerQuery(query.getId(), teacher.getId(), answerQueryDTO)

        then: "exception User didn't answer question"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.TEACHER_NOT_IN_COURSE
    }

    @TestConfiguration
    static class AnswerQueryServiceImplTestContextConfiguration {

        @Bean
        AnswerQueryService answerQueryService() {
            return new AnswerQueryService()
        }
    }
}
