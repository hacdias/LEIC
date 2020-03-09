package pt.ulisboa.tecnico.socialsoftware.tutor.query.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.query.QueryService
import pt.ulisboa.tecnico.socialsoftware.tutor.query.dto.QueryDto
import pt.ulisboa.tecnico.socialsoftware.tutor.query.repository.QueryRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification

@DataJpaTest
class CreateQueryTest extends Specification {
    public static final String COURSE_NAME = "Software Architecture"
    public static final String ACRONYM = "AS1"
    public static final String ACADEMIC_TERM = "1 SEM"
    public static final String QUESTION_TITLE = 'question title'
    public static final String QUESTION_CONTENT = 'question content'
    public static final String QUESTION_TITLE_2 = 'question title 2'
    public static final String QUESTION_CONTENT_2 = 'question content 2'
    public static final String OPTION_CONTENT = "optionId content"
    public static final String USER_NAME = "Student Name"
    public static final String USER_USERNAME = "Student Username"
    public static final String QUERY_TITLE = 'query title'
    public static final String QUERY_CONTENT = 'query content'
    public static final String QUERY_TITLE_2 = 'query title 2'
    public static final String QUERY_CONTENT_2 = 'query content 2'

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


    def course
    def courseExecution
    def question
    def question2
    def student

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

        question2 = new Question()
        question2.setKey(2)
        question2.setCourse(course)
        question2.setTitle(QUESTION_TITLE_2)
        question2.setContent(QUESTION_CONTENT_2)
        question2.setStatus(Question.Status.AVAILABLE)
        course.addQuestion(question2)
        questionRepository.save(question2)

        student = new User(USER_NAME, USER_USERNAME, 1, User.Role.STUDENT)
        student.getCourseExecutions().add(courseExecution)
        courseExecution.getUsers().add(student)
        userRepository.save(student)

    }

    def "create a query with a title and a description"() {
        given: "a queryDTO"
        def queryDTO = new QueryDto()
        queryDTO.setKey(1)
        queryDTO.setTitle(QUERY_TITLE)
        queryDTO.setContent(QUERY_CONTENT)

        when:
        queryService.createQuery(question.getId(), student.getId(), queryDTO)

        then: "the correct query is inside the repository"
        queryRepository.count() == 1L
        def result = queryRepository.findAll().get(0)
        result.getId() != null
        result.getKey() == 1
        result.getTitle() == QUERY_TITLE
        result.getContent() == QUERY_CONTENT
        result.getQuestion().getTitle() == QUESTION_TITLE
        result.getQuestion().getContent() == QUESTION_CONTENT
        result.getStudent().getName() == USER_NAME
        result.getStudent().getUsername() == USER_USERNAME
        question.getQueries().contains(result)
        student.getQueries().contains(result)
    }

    def "create two queries on different questions"() {
        given: "a queryDTO"
        def queryDTO1 = new QueryDto()
        queryDTO1.setKey(1)
        queryDTO1.setTitle(QUERY_TITLE)
        queryDTO1.setContent(QUERY_CONTENT)
        and: "another queryDTO"
        def queryDTO2 = new QueryDto()
        queryDTO2.setKey(2)
        queryDTO2.setTitle(QUERY_TITLE_2)
        queryDTO2.setContent(QUERY_CONTENT_2)

        when:
        queryService.createQuery(question.getId(), student.getId(), queryDTO1)
        queryService.createQuery(question2.getId(), student.getId(), queryDTO2)

        then: "the two queries are inside the repository"
        queryRepository.count() == 2L
        def result = queryRepository.findAll()
        def result1 = result.get(0)
        def result2 = result.get(1)

        and: "the first query is properly saved"
        result1.getId() != null
        result1.getKey() == 1
        result1.getTitle() == QUERY_TITLE
        result1.getContent() == QUERY_CONTENT
        result1.getQuestion().getTitle() == QUESTION_TITLE
        result1.getQuestion().getContent() == QUESTION_CONTENT
        result1.getStudent().getName() == USER_NAME
        result1.getStudent().getUsername() == USER_USERNAME
        question.getQueries().contains(result1)
        student.getQueries().contains(result1)

        and: "the second query is properly saved"
        result2.getId() != null
        result2.getKey() == 2
        result2.getTitle() == QUERY_TITLE_2
        result2.getContent() == QUERY_CONTENT_2
        result2.getQuestion().getTitle() == QUESTION_TITLE_2
        result2.getQuestion().getContent() == QUESTION_CONTENT_2
        result2.getStudent().getName() == USER_NAME
        result2.getStudent().getUsername() == USER_USERNAME
        question2.getQueries().contains(result2)
        student.getQueries().contains(result2)
    }

    def "create two queries on same question"() {
        given: "a queryDTO"
        def queryDTO1 = new QueryDto()
        queryDTO1.setKey(1)
        queryDTO1.setTitle(QUERY_TITLE)
        queryDTO1.setContent(QUERY_CONTENT)
        and: "another queryDTO"
        def queryDTO2 = new QueryDto()
        queryDTO2.setKey(2)
        queryDTO2.setTitle(QUERY_TITLE_2)
        queryDTO2.setContent(QUERY_CONTENT_2)

        when:
        queryService.createQuery(question.getId(), student.getId(), queryDTO1)
        queryService.createQuery(question.getId(), student.getId(), queryDTO2)

        then: "the two queries are inside the repository"
        queryRepository.count() == 2L
        def result = queryRepository.findAll()
        def result1 = result.get(0)
        def result2 = result.get(1)

        and: "the first query is properly saved"
        result1.getId() != null
        result1.getKey() == 1
        result1.getTitle() == QUERY_TITLE
        result1.getContent() == QUERY_CONTENT
        result1.getQuestion().getTitle() == QUESTION_TITLE
        result1.getQuestion().getContent() == QUESTION_CONTENT
        result1.getStudent().getName() == USER_NAME
        result1.getStudent().getUsername() == USER_USERNAME
        question.getQueries().contains(result1)
        student.getQueries().contains(result1)

        and: "the second query is properly saved"
        result2.getId() != null
        result2.getKey() == 2
        result2.getTitle() == QUERY_TITLE_2
        result2.getContent() == QUERY_CONTENT_2
        result2.getQuestion().getTitle() == QUESTION_TITLE
        result2.getQuestion().getContent() == QUESTION_CONTENT
        result2.getStudent().getName() == USER_NAME
        result2.getStudent().getUsername() == USER_USERNAME
        question.getQueries().contains(result2)
        student.getQueries().contains(result2)
    }

    @TestConfiguration
    static class QueryServiceImplTestContextConfiguration {

        @Bean
        QueryService queryService() {
            return new QueryService()
        }
    }
}
