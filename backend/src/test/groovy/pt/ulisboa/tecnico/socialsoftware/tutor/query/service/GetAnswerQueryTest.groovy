package pt.ulisboa.tecnico.socialsoftware.tutor.query.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.query.AnswerQueryService
import pt.ulisboa.tecnico.socialsoftware.tutor.query.domain.AnswerQuery
import pt.ulisboa.tecnico.socialsoftware.tutor.query.domain.Query
import pt.ulisboa.tecnico.socialsoftware.tutor.query.repository.AnswerQueryRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.query.repository.QueryRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification

@DataJpaTest
class GetAnswerQueryTest extends Specification {
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
        query.setKey(1)
        query.setTitle(QUERY_TITLE)
        query.setContent(QUERY_CONTENT)
        query.setQuestion(question)
        query.setStudent(student)
        student.addQuery(query)
        question.addQuery(query)
        queryRepository.save(query)

        answerQuery = new AnswerQuery()
        answerQuery.setKey(1)
        answerQuery.setContent(ANSWER_QUERY_CONTENT)
        answerQuery.setQuery(query)
        answerQuery.setTeacher(teacher)
        teacher.addAnswer(answerQuery)
        query.addAnswer(answerQuery)
        answerQueryRepository.save(answerQuery)
    }

    def 'get answers to a query'() {
        when:
        def answerQueryDtos = answerQueryService.getAnswersToQuery(query.getId())

        then: 'the query answer is retrieved'
        answerQueryRepository.count() == 1L
        def result = answerQueryRepository.findAll().get(0)
        result.getTeacher() == teacher
        teacher.getQueryAnswers().size() == 1
        result.getQuery() == query
        query.getAnswers().size() == 1
        and: 'the return statement contains one answer'
        answerQueryDtos.size() == 1
        def answerResult = answerQueryDtos.get(0)
        answerResult.getContent() == answerQuery.getContent()
        answerResult.getId() == result.getId()
    }

    def 'get answers by a teacher'() {
        when:
        def answerQueryDtos = answerQueryService.getAnswersByTeacher(teacher.getId())

        then: 'the query answer is retrieved'
        answerQueryRepository.count() == 1L
        def result = answerQueryRepository.findAll().get(0)
        result.getTeacher() == teacher
        teacher.getQueryAnswers().size() == 1
        result.getQuery() == query
        query.getAnswers().size() == 1
        and: 'the return statement contains one answer'
        answerQueryDtos.size() == 1
        def answerResult = answerQueryDtos.get(0)
        answerResult.getContent() == answerQuery.getContent()
        answerResult.getId() == result.getId()
    }

    @TestConfiguration
    static class AnswerQueryServiceImplTestContextConfiguration {

        @Bean
        AnswerQueryService answerQueryService() {
            return new AnswerQueryService()
        }
    }
}
