package pt.ulisboa.tecnico.socialsoftware.tutor.tournament;

//--------External Imports--------
import java.util.List;
import java.util.Random;
import java.util.Comparator;
import java.util.Collections;
import java.util.stream.Collectors;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

//--------Internal Imports--------
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto.TournamentDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.Tournament;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.repository.TournamentRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.TopicRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto.StatementQuizDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuizAnswerRepository;


@Service
public class TournamentService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseExecutionRepository courseExecutionRepository;

    @Autowired
    private TournamentRepository tournamentRepository;

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private QuizAnswerRepository quizAnswerRepository;

    @PersistenceContext
    EntityManager entityManager;

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @org.springframework.transaction.annotation.Transactional(isolation = Isolation.REPEATABLE_READ)
    public TournamentDto createTournament(Integer executionId, Integer studentId, TournamentDto tournamentDto) {
        
        User student = userRepository.findById(studentId).orElseThrow(() -> new TutorException(ErrorMessage.USER_NOT_FOUND, studentId));
        CourseExecution courseExecution = courseExecutionRepository.findById(executionId).orElseThrow(() -> new TutorException(ErrorMessage.COURSE_EXECUTION_NOT_FOUND, executionId));

        if (student.getRole() != User.Role.STUDENT) {
            throw new TutorException(ErrorMessage.USER_NOT_STUDENT, studentId);
        }

        if (!student.getCourseExecutions().contains(courseExecution)) {
            throw new TutorException(ErrorMessage.USER_NOT_ENROLLED, student.getUsername()); 
        }

        Tournament tournament = new Tournament(student, tournamentDto);
        tournament.setCourseExecution(courseExecution);

        if (tournamentDto.getTopics().size() != 0) {
            for (TopicDto topicDto : tournamentDto.getTopics()) {
                Topic topic = topicRepository.findById(topicDto.getId())
                        .orElseThrow(() -> new TutorException(ErrorMessage.TOPIC_NOT_FOUND, topicDto.getId()));
                tournament.addTopic(topic);
            }
        } else {
            throw new TutorException(ErrorMessage.TOPIC_NOT_SELECTED); 
        }

        if (tournamentDto.getCreationDate() == null) {
            tournament.setCreationDate(LocalDateTime.now());
        } else {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            tournament.setCreationDate(LocalDateTime.parse(tournamentDto.getCreationDate(), formatter));
        }

        this.entityManager.persist(tournament);
        return new TournamentDto(tournament);
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @org.springframework.transaction.annotation.Transactional(isolation = Isolation.REPEATABLE_READ)
    public void removeTournament(Integer studentId, Integer tournamentId) { 
        Tournament tournament = tournamentRepository.findById(tournamentId).orElseThrow(() -> new TutorException(ErrorMessage.TOURNAMENT_NOT_FOUND, tournamentId));
        User student = userRepository.findById(studentId).orElseThrow(() -> new TutorException(ErrorMessage.USER_NOT_FOUND, studentId));

        if (student.getRole() != User.Role.STUDENT) {
            throw new TutorException(ErrorMessage.USER_NOT_STUDENT, studentId);
        }

        if (tournament.getStudent() != student || !student.getCreatedTournaments().contains(tournament)) {
            throw new TutorException(ErrorMessage.STUDENT_NOT_CREATOR, studentId, tournamentId);
        }

        tournament.remove();
        entityManager.remove(tournament);
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @org.springframework.transaction.annotation.Transactional(isolation = Isolation.REPEATABLE_READ)
    public TournamentDto addEnrolledStudentToTournament(Integer studentId, Integer tournamentId) {
        Tournament tournament = tournamentRepository.findById(tournamentId).orElseThrow(() -> new TutorException(ErrorMessage.TOURNAMENT_NOT_FOUND, tournamentId));
        User student = userRepository.findById(studentId).orElseThrow(() -> new TutorException(ErrorMessage.USER_NOT_FOUND, studentId));

        if (student.getRole() != User.Role.STUDENT) {
            throw new TutorException(ErrorMessage.USER_NOT_STUDENT, studentId);
        }
        if (!student.getCourseExecutions().contains(tournament.getCourseExecution())) {
            throw new TutorException(ErrorMessage.TOURNAMENT_NOT_FOUND);
        }
        
        tournament.addEnrolledStudent(student);
        return new TournamentDto(tournament);
        
    }

    @Retryable(
        value = { SQLException.class },
        backoff = @Backoff(delay = 5000))
    @org.springframework.transaction.annotation.Transactional(isolation = Isolation.REPEATABLE_READ)
    public UserDto getCreator(Integer tournamentId) { 
        Tournament tournament = tournamentRepository.findById(tournamentId).orElseThrow(() -> new TutorException(ErrorMessage.TOURNAMENT_NOT_FOUND, tournamentId));
        
        return new UserDto(tournament.getStudent());
    }

    @Retryable(
        value = { SQLException.class },
        backoff = @Backoff(delay = 5000))
    @org.springframework.transaction.annotation.Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<TournamentDto> getOpenTournaments(Integer studentId, Integer executionId) { 
        courseExecutionRepository.findById(executionId).orElseThrow(() -> new TutorException(ErrorMessage.COURSE_EXECUTION_NOT_FOUND, executionId));
        User student = userRepository.findById(studentId).orElseThrow(() -> new TutorException(ErrorMessage.USER_NOT_FOUND, studentId));

        Comparator<Tournament> comparator = Comparator.comparing(Tournament::getAvailableDate, Comparator.nullsFirst(Comparator.reverseOrder()));

        return tournamentRepository.findTournaments(executionId).stream()
                .filter(tournament -> (tournament.getStudent().getUsername() == student.getUsername() || tournament.getConclusionDate() == null || LocalDateTime.now().isBefore(tournament.getConclusionDate())))
                .filter(tournament -> tournament.getCourseExecution().getId() == executionId)
                .filter(tournament -> (tournament.getStudent().getUsername() == student.getUsername() || tournament.getAvailableDate() == null || tournament.getAvailableDate().isBefore(LocalDateTime.now())))
                .sorted(comparator)
                .map(tournament -> new TournamentDto(tournament))
                .collect(Collectors.toList());
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @org.springframework.transaction.annotation.Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<TournamentDto> getEnrolledTournaments(Integer studentId, Integer executionId) {
        courseExecutionRepository.findById(executionId).orElseThrow(() -> new TutorException(ErrorMessage.COURSE_EXECUTION_NOT_FOUND, executionId));
        User student = userRepository.findById(studentId).orElseThrow(() -> new TutorException(ErrorMessage.USER_NOT_FOUND, studentId));

        Comparator<Tournament> comparator = Comparator.comparing(Tournament::getAvailableDate, Comparator.nullsFirst(Comparator.reverseOrder()));

        return tournamentRepository.findTournaments(executionId).stream()
                .filter(tournament -> (tournament.getEnrolledStudents().contains(student) ))
                .filter(tournament -> tournament.getCourseExecution().getId() == executionId)
                .sorted(comparator)
                .map(tournament -> new TournamentDto(tournament))
                .collect(Collectors.toList());
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @org.springframework.transaction.annotation.Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<TournamentDto> getCreatedTournaments(Integer studentId, Integer executionId) {
        courseExecutionRepository.findById(executionId).orElseThrow(() -> new TutorException(ErrorMessage.COURSE_EXECUTION_NOT_FOUND, executionId));
        User student = userRepository.findById(studentId).orElseThrow(() -> new TutorException(ErrorMessage.USER_NOT_FOUND, studentId));

        Comparator<Tournament> comparator = Comparator.comparing(Tournament::getAvailableDate, Comparator.nullsFirst(Comparator.reverseOrder()));

        return tournamentRepository.findTournaments(executionId).stream()
                .filter(tournament -> (tournament.getStudent().getId() == student.getId()))
                .filter(tournament -> tournament.getCourseExecution().getId() == executionId)
                .sorted(comparator)
                .map(tournament -> new TournamentDto(tournament))
                .collect(Collectors.toList());
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @org.springframework.transaction.annotation.Transactional(isolation = Isolation.REPEATABLE_READ)
    public TournamentDto generateTournamentQuiz(Integer tournamentId) {
        Tournament tournament = tournamentRepository.findById(tournamentId).orElseThrow(() -> new TutorException(ErrorMessage.TOURNAMENT_NOT_FOUND, tournamentId));

        if (tournament.getStatus() != Tournament.TournamentStatus.CAN_GENERATE_QUIZ) {
            throw new TutorException(ErrorMessage.CANNOT_GENERATE_TOURNAMENT_QUIZ, tournamentId);
        }

        Quiz quiz = new Quiz();
        quiz.setType(Quiz.QuizType.GENERATED.toString());

        List<Question> availableQuestions = questionRepository.findAvailableQuestions(tournament.getCourseExecution().getCourse().getId());

        if (availableQuestions.size() > 0) {
            availableQuestions = filterByTopic(availableQuestions, tournament);
        }

        if (availableQuestions.size() < tournament.getNumberQuestions()) {
            throw new TutorException(ErrorMessage.NOT_ENOUGH_QUESTIONS);
        }

        Random rand = new Random(System.currentTimeMillis());
        while (availableQuestions.size() > tournament.getNumberQuestions()) {
            int rng = rand.nextInt(availableQuestions.size());
            availableQuestions.remove(rng);
        }

        quiz.generate(availableQuestions);
        quiz.setTitle("Tournament '" + tournament.getTitle() + "' quiz");
        quiz.setCourseExecution(tournament.getCourseExecution());
        tournament.getCourseExecution().addQuiz(quiz);

        quizRepository.save(quiz);

        tournament.quiz = quizRepository.findById(quiz.getId()).orElseThrow(() -> new TutorException(ErrorMessage.QUIZ_NOT_FOUND, quiz.getId()));;
        tournament.setStatus(Tournament.TournamentStatus.QUIZ_GENERATED.toString());

        return new TournamentDto(tournament);
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public StatementQuizDto getTournamentQuiz(int userId, int tournamentId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new TutorException(ErrorMessage.USER_NOT_FOUND, userId));
        Tournament tournament = tournamentRepository.findById(tournamentId).orElseThrow(() -> new TutorException(ErrorMessage.TOURNAMENT_NOT_FOUND, tournamentId));
        Quiz quiz = tournament.getQuiz();

        if (quiz == null) {
            throw new TutorException(ErrorMessage.INVALID_STATUS_FOR_TOURNAMENT);
        }

        QuizAnswer quizAnswer = new QuizAnswer(user, quiz);
        quizAnswerRepository.save(quizAnswer);
        return new StatementQuizDto(quizAnswer);
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @org.springframework.transaction.annotation.Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<Question> filterByTopic(List<Question> questions, Tournament tournament) {

        return questions.stream()
                .filter(question -> !Collections.disjoint(question.getTopics(), tournament.getTopics()))
                .collect(Collectors.toList());
    }
}