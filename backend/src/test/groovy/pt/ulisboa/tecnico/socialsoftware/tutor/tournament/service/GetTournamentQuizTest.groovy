package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.service

//-----------External Imports-----------
import spock.lang.Specification
import org.springframework.context.annotation.Bean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest

//-----------Internal Imports-----------
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentService
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.Tournament
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.repository.TournamentRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.TopicRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;


@DataJpaTest
class GetTournamentQuizTest extends Specification {
    public static final String COURSE_NAME = "Software Architecture"
    public static final String ACRONYM = "AS1"
    public static final String ACADEMIC_TERM = "1 SEM"
    public static final String TOPIC_NAME = "Topic 1"
    public static final String QUESTION_TITLE = "Question 1"
    public static final String QUESTION_TITLE_2 = "Question 2"
    public static final String QUESTION_TITLE_3 = "Question 3"
    public static final String QUESTION_CONTENT = "Content 1"
    public static final String QUESTION_CONTENT_2 = "Content 2"
    public static final String QUESTION_CONTENT_3 = "Content 3"
    public static final String STUDENT_NAME = "Student 1"
    public static final String STUDENT_USERNAME = "std1"

    @Autowired
    CourseRepository courseRepository

    @Autowired
    CourseExecutionRepository courseExecutionRepository

    @Autowired
    TournamentRepository tournamentRepository

    @Autowired
    TournamentService tournamentService

    @Autowired
    UserRepository userRepository

    @Autowired
    TopicRepository topicRepository

    @Autowired
    TopicRepository questionRepository

    def course
    def courseExecution
    def tournament
    def question
    def question2
    def question3
    def topic
    def student

    def setup() {

        course = new Course(COURSE_NAME, Course.Type.TECNICO)
        courseRepository.save(course)

        courseExecution = new CourseExecution(course, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO)
        courseExecutionRepository.save(courseExecution)

        student = new User(STUDENT_NAME, STUDENT_USERNAME, 1, User.Role.STUDENT)
        student.getCourseExecutions().add(courseExecution)
        courseExecution.getUsers().add(student)
        userRepository.save(student)

        topic = new Topic()
        topic.setName(TOPIC_NAME)
        topicRepository.save(topic)
        course.addTopic(topic)

        question = new Question()
        question.setCourse(course)
        question.setTitle(QUESTION_TITLE)
        question.setContent(QUESTION_CONTENT)
        question.setStatus(Question.Status.AVAILABLE)
        question.addTopic(topic)
        course.addQuestion(question)
        questionRepository.save(question)

        question2 = new Question()
        question2.setCourse(course)
        question2.setTitle(QUESTION_TITLE_2)
        question2.setContent(QUESTION_CONTENT_2)
        question2.setStatus(Question.Status.AVAILABLE)
        question2.addTopic(topic)
        course.addQuestion(question2)
        questionRepository.save(question2)

        question3 = new Question()
        question3.setCourse(course)
        question3.setTitle(QUESTION_TITLE_3)
        question3.setContent(QUESTION_CONTENT_3)
        question3.setStatus(Question.Status.AVAILABLE)
        question3.addTopic(topic)
        course.addQuestion(question3)
        questionRepository.save(question3)

        tournament = new Tournament()
        tournament.setCourseExecution(courseExecution)
        tournament.addTopic(topic)
        courseExecution.addTournament(tournament)
        tournamentRepository.save(tournament)

        topic.addQuestion(question)
        topic.addQuestion(question2)
        topic.addQuestion(question3)

    }

    def "tournament quiz is fetched"() {

        given: "a tournament with 2 questions"
        tournament.setNumberQuestions(2)
        tournament.setStatus(Tournament.TournamentStatus.CAN_GENERATE_QUIZ.toString())
        tournamentService.generateTournamentQuiz(tournament.getId());
        tournamentService.addEnrolledStudentToTournament(student.getId(), tournament.getId())

        when:
        def result = tournamentService.getTournamentQuiz(student.getId(), tournament.getId());

        then: "quiz is successfully fetched"
        result != null
        result.getId() == tournament.getQuiz().getId()


    }

    @TestConfiguration
    static class TournamentServiceImplTestContextConfiguration {
        @Bean
        TournamentService tournamentService() {
            return new TournamentService()
        }
    }
}