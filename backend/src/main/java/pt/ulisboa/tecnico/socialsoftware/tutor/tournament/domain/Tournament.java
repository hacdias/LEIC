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


@Entity
@Table(
        name = "tournaments",
        indexes = {@Index(name = "tournaments_indx_0", columnList = "key")
        })
public class Tournament {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique=true, nullable = false)
    private Integer key;

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

    @ManyToMany(cascade = CascadeType.ALL, mappedBy = "tournaments",  fetch=FetchType.LAZY)
    public Set<Topic> topics = new HashSet<>();
    
    @ManyToMany(cascade = CascadeType.ALL, mappedBy = "enrolledTournaments", fetch=FetchType.LAZY)  
    public Set<User> enrolledStudents = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "course_execution_id")
    private CourseExecution courseExecution;      
    
    public Tournament() {}

    public Tournament(User student, TournamentDto tournamentDto) {
        this.key = tournamentDto.getKey();
        this.creationDate = tournamentDto.getCreationDateDate();

        setNumberQuestions(tournamentDto.getNumberQuestions());
        setStudent(student);
        addEnrolledStudent(student);
        setAvailableDate(tournamentDto.getAvailableDateDate());
        setConclusionDate(tournamentDto.getConclusionDateDate());
        setTitle(tournamentDto.getTitle());
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getKey() {
        return key;
    }

    public void setKey(Integer key) {
        this.key = key;
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
    }

    public void addTopic(Topic topic) {
        this.topics.add(topic);
        topic.addTournament(this);
    }

    @Override
    public String toString() {
        return "Tournament{" +
                "id=" + id +
                "key=" + key +
                "student=" + student.getName() +
                ", creationDate=" + creationDate +
                ", availableDate=" + availableDate +
                ", conclusionDate=" + conclusionDate +
                ", title='" + title + '\'' +
                ", numberQuestions='" + numberQuestions + '\'' +
                ", topics='" + topics + '\'' +
                '}';
    }

    private void checkTitle(String title) {
        if (title == null || title.trim().length() == 0) {
            throw new TutorException(TOURNAMENT_NOT_CONSISTENT, "Title");
        }
    }

    private void checkNumberQuestions(Integer numberQuestions) {
        if (numberQuestions < 1) {
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
            throw new TutorException(TOURNAMENT_NOT_CONSISTENT, "Available date");
        }
        if (this.availableDate != null && this.conclusionDate != null && conclusionDate.isBefore(availableDate)) {
            throw new TutorException(TOURNAMENT_NOT_CONSISTENT, "Available date");
        }
    }

    public void remove() {
        //checkCanChange(); TO DO: MAYBE NECESSARY
        student.getCreatedTournaments().remove(this);
        student= null;

        getTopics().forEach(topic -> topic.getTournaments().remove(this));
        getTopics().clear();

        getEnrolledStudents().forEach(student -> student.getEnrolledTournaments().remove(this));
        getEnrolledStudents().clear();

        courseExecution.getTournaments().remove(this);
        courseExecution = null;
    }
}
