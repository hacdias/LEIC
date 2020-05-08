package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain;

//------------External Imports------------
import java.util.Set;
import java.util.HashSet;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// ------------Internal Imports------------
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto.TournamentDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;


@Entity
@Table(
        name = "tournaments"
        )
public class Tournament {

    public enum TournamentStatus {
        CAN_NOT_GENERATE_QUIZ, CAN_GENERATE_QUIZ, QUIZ_GENERATED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User student;

    @Column(name = "creation_date")
    private LocalDateTime creationDate;

    @Column(name = "available_date")
    private LocalDateTime availableDate;

    @Column(name = "conclusion_date")
    private LocalDateTime conclusionDate;

    @Column(nullable = false)
    private String title = "Title";

    @Column(name = "number_questions")
    private Integer numberQuestions;

    @Enumerated(EnumType.STRING)
    private TournamentStatus status = TournamentStatus.CAN_NOT_GENERATE_QUIZ;

    @ManyToMany(cascade = CascadeType.ALL, mappedBy = "tournaments",  fetch=FetchType.LAZY)
    public Set<Topic> topics = new HashSet<>();
    
    @ManyToMany(cascade = CascadeType.ALL, mappedBy = "enrolledTournaments", fetch=FetchType.LAZY)
    public Set<User> enrolledStudents = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_execution_id")
    private CourseExecution courseExecution;



    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "quiz_id")
    public Quiz quiz = null;
    
    public Tournament() {}

    public Tournament(User student, TournamentDto tournamentDto) {
        this.creationDate = tournamentDto.getCreationDateDate();

        setNumberQuestions(tournamentDto.getNumberQuestions());
        setStudent(student);
        setAvailableDate(tournamentDto.getAvailableDateDate());
        setConclusionDate(tournamentDto.getConclusionDateDate());
        setTitle(tournamentDto.getTitle());
        setStatus(tournamentDto.getStatus());
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public User getStudent() {
        return student;
    }

    public void setStudent(User student) {
        this.student = student;
        student.addCreatedTournament(this);
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public LocalDateTime getAvailableDate() {
        return availableDate;
    }

    public void setAvailableDate(LocalDateTime availableDate) {
        checkAvailableDate(availableDate);     
        this.availableDate = availableDate;
    }

    public LocalDateTime getConclusionDate() {
        return conclusionDate;
    }

    public void setConclusionDate(LocalDateTime conclusionDate) {
        checkConclusionDate(conclusionDate);         
        this.conclusionDate = conclusionDate;
    }    

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        checkTitle(title);
        this.title = title;
    }

    public TournamentStatus getStatus() {
        return status;
    }

    public void setStatus(String status) {
        if (status != null) {
            try {
                this.status = TournamentStatus.valueOf(status);
            } catch (IllegalArgumentException e) {
                throw new TutorException(INVALID_STATUS_FOR_TOURNAMENT);
            }
        }
    }

    public Integer getNumberQuestions() {
        return numberQuestions;
    }

    public void setNumberQuestions(Integer numberQuestions) {
        checkNumberQuestions(numberQuestions);
        this.numberQuestions = numberQuestions;
    }

    public Set<Topic> getTopics() {              
        return topics;
    }

    public void setTopics(Set<Topic> topics) {            
        this.topics = topics;
        topics.forEach(topic -> topic.addTournament(this));   
    }

    public CourseExecution getCourseExecution() {
        return courseExecution;
    }

    public void setCourseExecution(CourseExecution courseExecution) {
        this.courseExecution = courseExecution;
        courseExecution.addTournament(this);
    }

    public Set<User> getEnrolledStudents() {
        return enrolledStudents;
    }

    public void addEnrolledStudent(User student) {
        this.enrolledStudents.add(student);
        student.addEnrolledTournament(this);
        if (this.enrolledStudents.size() > 1) {
            this.status = TournamentStatus.CAN_GENERATE_QUIZ;
        }
    }

    public void addTopic(Topic topic) {
        this.topics.add(topic);
        topic.addTournament(this);
    }

    public void setStatus(TournamentStatus status) {
        this.status = status;
    }

    private void checkTitle(String title) {
        if (title == null || title.trim().length() == 0) {
            throw new TutorException(TOURNAMENT_NOT_CONSISTENT, "Title");
        }
    }

    private void checkNumberQuestions(Integer numberQuestions) {
        if (numberQuestions == null || numberQuestions < 1) {
            throw new TutorException(TOURNAMENT_NOT_CONSISTENT, "Number of questions");
        }
    }

    private void checkAvailableDate(LocalDateTime availableDate) {
        if (availableDate == null) {
            throw new TutorException(TOURNAMENT_NOT_CONSISTENT, "Available date");
        }
        if (this.availableDate != null && this.conclusionDate != null && conclusionDate.isBefore(availableDate)) {
            throw new TutorException(TOURNAMENT_NOT_CONSISTENT, "Available date");
        }
    }

    private void checkConclusionDate(LocalDateTime conclusionDate) {
        if (conclusionDate == null) {
            throw new TutorException(TOURNAMENT_NOT_CONSISTENT, "Conclusion date");
        }
        if (this.availableDate != null && this.conclusionDate != null && conclusionDate.isBefore(availableDate)) {
            throw new TutorException(TOURNAMENT_NOT_CONSISTENT, "Conclusion date");
        }
    }

    public Quiz getQuiz() {
        return quiz;
    }

    public void setQuiz(Quiz quiz) {
        this.quiz = quiz;
        quiz.setTournament(this);
    }

    public void remove() {
        student.getCreatedTournaments().remove(this);
        student = null;

        getTopics().forEach(topic -> topic.getTournaments().remove(this));
        getTopics().clear();

        getEnrolledStudents().forEach(student -> student.getEnrolledTournaments().remove(this));
        getEnrolledStudents().clear();

        courseExecution.getTournaments().remove(this);
        courseExecution = null;
    }

    @Override
    public String toString() {
        return "Tournament{" +
                "id=" + id +
                ", student=" + student.getName() +
                ", status=" + status +
                ", creationDate=" + creationDate +
                ", availableDate=" + availableDate +
                ", conclusionDate=" + conclusionDate +
                ", title='" + title + '\'' +
                ", numberQuestions='" + numberQuestions + '\'' +
                '}';
    }
}
