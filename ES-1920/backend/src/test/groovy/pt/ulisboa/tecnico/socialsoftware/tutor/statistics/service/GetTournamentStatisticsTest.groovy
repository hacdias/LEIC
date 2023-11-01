package pt.ulisboa.tecnico.socialsoftware.tutor.statistics.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.transaction.annotation.Transactional
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuestionAnswerRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuizAnswerRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.Tournament
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.repository.TournamentRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.OptionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizQuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.statistics.StatsService
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.TopicRepository
import spock.lang.Specification

@DataJpaTest
@Transactional
class GetTournamentStatisticsTest extends Specification {
    public static final String COURSE_NAME = "Software Architecture"
    public static final String ACRONYM = "AS1"
    public static final String ACADEMIC_TERM = "1 SEM"
    public static final String QUESTION_TITLE = 'a question title'
    public static final String QUESTION_CONTENT = 'a question content'
    public static final String STUDENT_NAME = "Anonymous User"
    public static final String STUDENT_USERNAME = "anon"
    public static final String TOPIC_NAME = 'topic'
    public static final String OPTION_CONTENT = 'option content'

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
    TopicRepository topicRepository

    @Autowired
    TournamentRepository tournamentRepository

    @Autowired
    OptionRepository optionRepository

    @Autowired
    StatsService statsService

    def course
    def courseExecution
    def student
    def question
    def quiz
    def quizQuestion
    def quizQuestion1
    def quizAnswer
    def tournament
    def topic
    def option
    def questionAnswer

    def setup () {
        course = new Course(COURSE_NAME, Course.Type.TECNICO)
        courseRepository.save(course)

        courseExecution = new CourseExecution(course, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO)
        courseExecutionRepository.save(courseExecution)

        topic = new Topic()
        topic.setName(TOPIC_NAME)
        topicRepository.save(topic)
        course.addTopic(topic)

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

        option = new Option()
        option.setSequence(0)
        option.setContent(OPTION_CONTENT)
        option.setCorrect(true)
        optionRepository.save(option)
        question.addOption(optionRepository.findAll().get(0))

        tournament = new Tournament()
        tournament.setStudent(student)
        tournament.addEnrolledStudent(student)
        tournament.setCourseExecution(courseExecution)
        courseExecution.addTournament(tournament)
        tournament.addTopic(topic)
        tournamentRepository.save(tournament)

    }

    def "get statistics and make sure tournament info is correct"() {
        given: "a tournament quiz answer with 1 correct answer and one incorrect"
        quiz = new Quiz()
        quiz.setType("GENERATED")
        quizRepository.save(quiz)
        tournament.setQuiz(quiz)
        quiz.setCourseExecution(courseExecution)
        courseExecution.addQuiz(quiz)

        quizQuestion = new QuizQuestion()
        quizQuestion.setQuestion(question)
        quizQuestion.setQuiz(quiz)
        quizQuestionRepository.save(quizQuestion)

        quizQuestion1 = new QuizQuestion()
        quizQuestion1.setQuestion(question)
        quizQuestion1.setQuiz(quiz)
        quizQuestionRepository.save(quizQuestion1)

        quizAnswer = new QuizAnswer(student, quiz)
        quizAnswerRepository.save(quizAnswer)

        questionAnswer = quizAnswer.getQuestionAnswers().get(0)
        questionAnswer.setOption(optionRepository.findAll().get(0))
        quizAnswer.setCompleted(true)

        when:
        def stats = statsService.getStats(userRepository.findAll().get(0).getId(), courseExecutionRepository.findAll().get(0).getId())

        then: "the statistics are correct"
        stats.getEnrolledTournaments() == 1
        stats.getCorrectTournamentAnswers() == 1
        stats.getTotalTournamentAnswers() == 2

    }

    @TestConfiguration
    static class TestContextConfiguration {

        @Bean
        StatsService statsService() {
            return new StatsService()
        }
    }
}