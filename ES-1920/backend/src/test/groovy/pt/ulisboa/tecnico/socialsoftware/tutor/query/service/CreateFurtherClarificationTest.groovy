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
import pt.ulisboa.tecnico.socialsoftware.tutor.query.domain.AnswerQuery
import pt.ulisboa.tecnico.socialsoftware.tutor.query.domain.Query
import pt.ulisboa.tecnico.socialsoftware.tutor.query.dto.AnswerQueryDto
import pt.ulisboa.tecnico.socialsoftware.tutor.query.repository.AnswerQueryRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.query.repository.QueryRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification
import spock.lang.Unroll

@DataJpaTest
class CreateFurtherClarificationTest extends Specification {

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
    public static final String FURTHER_CLARIFICATION_CONTENT_STUDENT = 'further clarification content (student)'
    public static final String FURTHER_CLARIFICATION_CONTENT_TEACHER = 'further clarification content (teacher)'
    public static final String ANOTHER_TEACHER_NAME = 'another teacher name'
    public static final String ANOTHER_TEACHER_USERNAME = 'another teacher username'

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
    def teacher
    def query
    def answerQuery

    def setup(){
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

        teacher = new User(TEACHER_NAME, TEACHER_USERNAME, 2, User.Role.TEACHER)
        teacher.getCourseExecutions().add(courseExecution)
        courseExecution.getUsers().add(teacher)
        userRepository.save(teacher)

        query = new Query()
        query.setTitle(QUERY_TITLE)
        query.setContent(QUERY_CONTENT)
        query.setQuestion(question)
        query.setStudent(student)
        student.addQuery(query)
        question.addQuery(query)
        queryRepository.save(query)

        answerQuery = new AnswerQuery()
        answerQuery.setContent(ANSWER_QUERY_CONTENT)
        answerQuery.setQuery(query)
        answerQuery.setTeacher(teacher)
        teacher.addAnswer(answerQuery)
        query.addAnswer(answerQuery)
        answerQueryRepository.save(answerQuery)
    }

    def "add further clarification to a answer by student"(){
        given: "create further clarification to an answer"
        def furtherClarificationDTO = new AnswerQueryDto(answerQuery)
        furtherClarificationDTO.setContent(FURTHER_CLARIFICATION_CONTENT_STUDENT)

        when:
        answerQueryService.addFurtherClarification(answerQuery.getId(), student.getId(), furtherClarificationDTO)

        then: "the correct further clarification is inside the repository"
        answerQueryRepository.count() == 2L
        def result = answerQueryRepository.findAll().get(1)
        result.getId() != null
        result.getContent() == FURTHER_CLARIFICATION_CONTENT_STUDENT
        result.getAnswerQuery().getContent() == ANSWER_QUERY_CONTENT
        result.getUser().getName() == STUDENT_NAME
        result.getUser().getUsername() == STUDENT_USERNAME
        answerQuery.getAnswers().contains(result)
        student.getQueryAnswers().contains(result)
    }

    def "add further clarification to a answer by teacher"(){
        given: "create further clarification to an answer"
        def furtherClarificationDTO = new AnswerQueryDto(answerQuery)
        furtherClarificationDTO.setContent(FURTHER_CLARIFICATION_CONTENT_TEACHER)

        when:
        answerQueryService.addFurtherClarification(answerQuery.getId(), teacher.getId(), furtherClarificationDTO)

        then: "the correct further clarification is inside the repository"
        answerQueryRepository.count() == 3L
        def result = answerQueryRepository.findAll().get(2)
        result.getId() != null
        result.getContent() == FURTHER_CLARIFICATION_CONTENT_TEACHER
        result.getAnswerQuery().getContent() == ANSWER_QUERY_CONTENT
        result.getUser().getName() == TEACHER_NAME
        result.getUser().getUsername() == TEACHER_USERNAME
        answerQuery.getAnswers().contains(result)
        student.getQueryAnswers().contains(result)
    }

    def "teacher doesn't teach course"() {
        given: "a teacher who doesn't teach the course"
        def anotherTeacher = new User(ANOTHER_TEACHER_NAME, ANOTHER_TEACHER_USERNAME, 3, User.Role.TEACHER)
        userRepository.save(anotherTeacher)

        and: "a furtherClarificationDto"
        def furtherClarificationDto = new AnswerQueryDto()
        furtherClarificationDto.setContent(FURTHER_CLARIFICATION_CONTENT_TEACHER)

        when:
        answerQueryService.addFurtherClarification(answerQuery.getId(), anotherTeacher.getId(), furtherClarificationDto)

        then: "exception User didn't answer question"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.TEACHER_NOT_IN_COURSE
    }

    @Unroll("invalid arguments: content=#content || errorMessage=#errorMessage ")
    def "update a query with missing information"() {
        given: "create a answer query"
        def furtherClarificationDto = new AnswerQueryDto()
        furtherClarificationDto.setContent(content)

        when:
        answerQueryService.addFurtherClarification(answerQuery.getId(), furtherClarificationDto)

        then: "exception query is missing data is thrown "
        def exception = thrown(TutorException)
        exception.getErrorMessage() == errorMessage

        where:
        content                         || errorMessage
        null                            || ErrorMessage.ANSWER_QUERY_MISSING_DATA
        "  "                            || ErrorMessage.ANSWER_QUERY_MISSING_DATA
    }

    @TestConfiguration
    static class AnswerQueryServiceImplTestContextConfiguration {

        @Bean
        AnswerQueryService answerQueryService() {
            return new AnswerQueryService()
        }
    }
}
