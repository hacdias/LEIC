package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain;

//------------External Imports------------
import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// ------------Internal Imports------------
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto.TournamentDto;


@Entity
@Table(
        name = "Tournament",
        indexes = {@Index(name = "Tournament_idx", columnList = "key")
        })

public class Tournament {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique=true, nullable = false)
    private Integer key;

    @Column(name = "creation_date")
    private LocalDateTime creationDate;

    @Column(name = "available_date")
    private LocalDateTime availableDate;

    @Column(name = "conclusion_date")
    private LocalDateTime conclusionDate;

    @Column(nullable = false)
    private String title = "Title";

    private Integer numberQuestions;

    //TO DO: missing something about topics

    //TO DO: misisng @something_to_something
    //@JoinColumn(name = "course_execution_id")
    //private CourseExecution courseExecution;      

    public Tournament() {}

    public Tournament(TournamentDto tournamentDto) {

        this.key = tournamentDto.getKey();
        this.creationDate = tournamentDto.getCreationDateDate();
        setAvailableDate(tournamentDto.getAvailableDateDate());
        setConclusionDate(tournamentDto.getConclusionDateDate());
        setTitle(tournamentDto.getTitle());
        this.numberQuestions = tournamentDto.getNumberQuestions();
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
        //checkAvailableDate(availableDate);           TO DO: make a decent check function
        this.availableDate = availableDate;
    }

    public LocalDateTime getConclusionDate() {
        return conclusionDate;
    }

    public void setConclusionDate(LocalDateTime conclusionDate) {
        //checkConclusionDate(conclusionDate);          TO DO: make a decent check function
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
        this.numberQuestions = numberQuestions;
    }

    //TO DO: missing something about topics
/*
    public CourseExecution getCourseExecution() {
        return courseExecution;
    }

    public void setCourseExecution(CourseExecution courseExecution) {
        this.courseExecution = courseExecution;
        //courseExecution.addTournament(this);      TO DO: add function to course execution           
    }*/

    @Override
    public String toString() {
        return "Tournament{" +
                "id=" + id +
                "key=" + key +
                ", creationDate=" + creationDate +
                ", availableDate=" + availableDate +
                ", conclusionDate=" + conclusionDate +
                ", title='" + title + '\'' +
                ", numberQuestions='" + numberQuestions + '\'' +
                '}';
    }

    private void checkTitle(String title) {
        if (title == null || title.trim().length() == 0) {
            //throw new TutorException(TOURNAMENT_NOT_CONSISTENT, "Title");  TO DO: add exception to TutorException
        }
    }

    /*
    public void remove() {
        checkCanChange();

        courseExecution.getTournaments().remove(this);
        courseExecution = null;
    }
    
    TO DO: it is probably relevant to implement this function/method
    */   

}
