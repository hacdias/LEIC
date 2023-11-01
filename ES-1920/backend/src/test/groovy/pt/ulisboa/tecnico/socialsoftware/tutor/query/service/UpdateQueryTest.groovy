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
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.query.QueryService
import pt.ulisboa.tecnico.socialsoftware.tutor.query.domain.AnswerQuery
import pt.ulisboa.tecnico.socialsoftware.tutor.query.domain.Query
import pt.ulisboa.tecnico.socialsoftware.tutor.query.dto.QueryDto
import pt.ulisboa.tecnico.socialsoftware.tutor.query.repository.AnswerQueryRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.query.repository.QueryRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizQuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification
import spock.lang.Unroll

@DataJpaTest
class UpdateQueryTest extends Specification {
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
    public static final String NEW_QUERY_TITLE = 'new query title'
    public static final String NEW_QUERY_CONTENT = 'new query content'
    public static final String ANSWER_QUERY_CONTENT = 'answer query content'

    @Autowired
    QueryService queryService

    @Autowired
    CourseRepository courseRepository

    @Autowired
    CourseExecutionRepository courseExecutionRepository

    @Autowired
    QuestionRepository questionRepository

    @Autowired
    QuizRepository quizRepository

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

    @Autowired
    AnswerQueryRepository answerQueryRepository

    def course
    def courseExecution
    def question
    def student
    def query
    def teacher
    def quiz
    def quizQuestion
    def quizAnswer
    def questionAnswer

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

        teacher = new User(TEACHER_NAME, TEACHER_USERNAME, 2, User.Role.TEACHER)
        teacher.getCourseExecutions().add(courseExecution)
        courseExecution.getUsers().add(teacher)
        userRepository.save(teacher)

        quiz = new Quiz()
        quiz.setType("TEST")
        quizRepository.save(quiz)

        quizQuestion = new QuizQuestion()
        quizQuestion.setQuestion(question)
        question.addQuizQuestion(quizQuestion)
        quizQuestion.setQuiz(quiz)
        quiz.addQuizQuestion(quizQuestion)
        quizQuestionRepository.save(quizQuestion)

        quizAnswer = new QuizAnswer()
        quizAnswer.setUser(student)
        student.addQuizAnswer(quizAnswer)
        quizAnswer.setQuiz(quiz)
        quiz.addQuizAnswer(quizAnswer)
        quizAnswerRepository.save(quizAnswer)

        questionAnswer = new QuestionAnswer()
        questionAnswer.setQuizAnswer(quizAnswer)
        questionAnswer.setQuizQuestion(quizQuestion)
        quizAnswer.addQuestionAnswer(questionAnswer)
        quizQuestion.addQuestionAnswer(questionAnswer)
        questionAnswerRepository.save(questionAnswer)

        query = new Query()
        query.setTitle(QUERY_TITLE)
        query.setContent(QUERY_CONTENT)
        query.setQuestion(question)
        question.addQuery(query)
        query.setStudent(student)
        student.addQuery(query)
        query.setQuestionAnswer(questionAnswer)
        questionAnswer.addQuery(query)
        queryRepository.save(query)
    }

    def "update a query with new information"() {
        given: "create a query"
        def queryDTO = new QueryDto(query)
        queryDTO.setTitle(NEW_QUERY_TITLE)
        queryDTO.setContent(NEW_QUERY_CONTENT)

        when:
        queryService.updateQuery(query.getId(), queryDTO)

        then: "the query is changed"
        queryRepository.count() == 1L
        def result = queryRepository.findAll().get(0)
        result.getId() == query.getId()
        result.getTitle() == NEW_QUERY_TITLE
        result.getContent() == NEW_QUERY_CONTENT
        and: 'are not changed'
        result.getStudent() == query.getStudent()
        result.getAnswers() == query.getAnswers()
        result.getCreationDate() == query.getCreationDate()
        result.getQuestion() == query.getQuestion()
        !result.getShared();
    }

    def "update a query with answers"() {
        given: "create a query"
        def queryDTO = new QueryDto(query)
        queryDTO.setTitle(NEW_QUERY_TITLE)
        queryDTO.setContent(NEW_QUERY_CONTENT)
        and: "answer to the the query"
        def answer = new AnswerQuery()
        answer.setContent(ANSWER_QUERY_CONTENT)
        answer.setQuery(query)
        answer.setTeacher(teacher)
        query.addAnswer(answer)
        teacher.addAnswer(answer)
        answerQueryRepository.save(answer)

        when:
        queryService.updateQuery(query.getId(), queryDTO)

        then: "the query an exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.QUERY_IS_ANSWERED
    }

    def "approve a query to shared"() {
        when:
        queryService.shareQuery(query.getId())

        then: "the query is changed"
        queryRepository.count() == 1L
        def result = queryRepository.findAll().get(0)
        result.getId() == query.getId()
        result.getShared();
        and: 'are not changed'
        result.getTitle() == QUERY_TITLE
        result.getContent() == QUERY_CONTENT
        result.getStudent() == query.getStudent()
        result.getAnswers() == query.getAnswers()
        result.getCreationDate() == query.getCreationDate()
        result.getQuestion() == query.getQuestion()
    }

    @Unroll("invalid arguments: content=#content | title=#title || errorMessage=#errorMessage ")
    def "update a query with missing information"() {
        given: "create a query"
        def queryDTO = new QueryDto(query)
        queryDTO.setTitle(title)
        queryDTO.setContent(content)

        when:
        queryService.updateQuery(query.getId(), queryDTO)

        then: "exception query is missing data is thrown "
        def exception = thrown(TutorException)
        exception.getErrorMessage() == errorMessage

        where:
        content                         | title                                 || errorMessage
        null                            | NEW_QUERY_TITLE                       || ErrorMessage.QUERY_MISSING_DATA
        "  "                            | NEW_QUERY_TITLE                       || ErrorMessage.QUERY_MISSING_DATA
        NEW_QUERY_CONTENT               | null                                  || ErrorMessage.QUERY_MISSING_DATA
        NEW_QUERY_CONTENT               | "  "                                  || ErrorMessage.QUERY_MISSING_DATA
    }

    @TestConfiguration
    static class QueryServiceImplTestContextConfiguration {

        @Bean
        QueryService queryService() {
            return new QueryService()
        }
    }
}
