package pt.ulisboa.tecnico.socialsoftware.tutor.statistics.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.query.QueryService
import pt.ulisboa.tecnico.socialsoftware.tutor.query.domain.Query
import pt.ulisboa.tecnico.socialsoftware.tutor.query.repository.QueryRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.statistics.StatsService
import pt.ulisboa.tecnico.socialsoftware.tutor.suggestions.SuggestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.suggestions.domain.Suggestion
import pt.ulisboa.tecnico.socialsoftware.tutor.suggestions.dto.SuggestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.suggestions.repository.SuggestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification

@DataJpaTest
@Transactional(propagation = Propagation.NOT_SUPPORTED)
class GetStatisticsTest extends Specification {
    public static final String COURSE_NAME = "Software Architecture"
    public static final String ACRONYM = "AS1"
    public static final String ACADEMIC_TERM = "1 SEM"
    public static final String QUESTION_TITLE = 'a question title'
    public static final String QUESTION_CONTENT = 'a question content'
    public static final String QUERY_TITLE = 'a query title'
    public static final String QUERY_CONTENT = 'a query content'
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
    QueryRepository queryRepository

    @Autowired
    SuggestionService suggestionService

    @Autowired
    QueryService queryService

    @Autowired
    UserRepository userRepository

    @Autowired
    StatsService statsService

    def course
    def courseExecution
    def student
    def questionDto
    def question

    def setup () {
        course = new Course(COURSE_NAME, Course.Type.TECNICO)
        courseRepository.save(course)

        courseExecution = new CourseExecution(course, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO)
        courseExecutionRepository.save(courseExecution)

        student = new User(STUDENT_NAME, STUDENT_USERNAME, 1, User.Role.STUDENT)
        student.getCourseExecutions().add(courseExecution)
        courseExecution.getUsers().add(student)
        userRepository.save(student)

        question = new Question()
        question.setKey(1)
        question.setCourse(course)
        question.setTitle(QUESTION_TITLE)
        question.setContent(QUESTION_CONTENT)
        question.setStatus(Question.Status.AVAILABLE)
        course.addQuestion(question)
        questionRepository.save(question)
    }

    def "get statistics and make sure suggestions info is correct"() {
        given: "2 suggestions, 1 approved"

        questionDto = new QuestionDto()
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_TITLE)
        questionDto.setContent(QUESTION_CONTENT)
        questionDto.setStatus(Question.Status.DISABLED.name())
        def optionDto = new OptionDto()
        optionDto.setContent(OPTION_CONTENT)
        optionDto.setCorrect(true)
        def options = new ArrayList<OptionDto>()
        options.add(optionDto)
        questionDto.setOptions(options)

        def suggestionDto = new SuggestionDto()
        suggestionDto.setStatus(Suggestion.Status.PENDING.name())
        suggestionDto.setQuestion(questionDto)

        def s1 = new Suggestion(student, course, suggestionDto)

        suggestionDto.setStatus(Suggestion.Status.APPROVED.name())
        def s2 = new Suggestion(student, course, suggestionDto)

        suggestionRepository.saveAndFlush(s1)
        suggestionRepository.saveAndFlush(s2)

        when:
        def stats = statsService.getStats(student.getId(), courseExecution.getId())

        then: "the statistics are correct"
        stats.getTotalProposedSuggestions() == 2
        stats.getApprovedProposedSuggestions() == 1
    }

    def "get statistics and make sure queries info is correct"() {
        given: "2 queries, 1 shared"

        def query = new Query()
        query.setTitle(QUERY_TITLE)
        query.setContent(QUERY_CONTENT)
        query.setQuestion(question)
        question.addQuery(query)
        query.setStudent(student)
        student.addQuery(query)
        query.setShared(false)
        queryRepository.save(query)

        query = new Query()
        query.setTitle(QUERY_TITLE)
        query.setContent(QUERY_CONTENT)
        query.setQuestion(question)
        question.addQuery(query)
        query.setStudent(student)
        student.addQuery(query)
        query.share()
        queryRepository.save(query)

        when:
        def stats = statsService.getStats(student.getId(), courseExecution.getId())

        then: "the statistics are correct"
        stats.getTotalQueriesSubmitted() == 2
        stats.getSharedQueries() == 1
    }

    @TestConfiguration
    static class TestContextConfiguration {

        @Bean
        QueryService queryService() {
            return new QueryService()
        }

        @Bean
        SuggestionService suggestionService() {
            return new SuggestionService()
        }

        @Bean
        QuestionService questionService() {
            return new QuestionService()
        }

        @Bean
        StatsService statsService() {
            return new StatsService()
        }
    }
}
