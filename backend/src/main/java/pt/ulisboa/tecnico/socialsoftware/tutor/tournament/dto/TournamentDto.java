package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto;

//------------External Imports------------
import java.util.Set;
import java.util.HashSet;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// ------------Internal Imports------------
import org.springframework.data.annotation.Transient;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.Tournament;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic;


public class TournamentDto implements Serializable {

    private Integer id;
    private Integer key;
    private User student;
    private String creationDate = null;
    private String availableDate = null;
    private String conclusionDate = null;
    private String title;
    private Integer numberQuestions;
    public Set<Topic> topics = new HashSet<>();                //TO DO: check

    @Transient
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

    public TournamentDto() {}

    public TournamentDto(Tournament tournament) {
        this.id = tournament.getId();
        this.key = tournament.getKey();
        this.student = tournament.getStudent();
        this.title = tournament.getTitle();
        this.numberQuestions = tournament.getNumberQuestions();
        this.topics = tournament.getTopics();                //TO DO: check

        if (tournament.getCreationDate() != null)
            this.creationDate = tournament.getCreationDate().format(formatter);
        if (tournament.getAvailableDate() != null)
            this.availableDate = tournament.getAvailableDate().format(formatter);
        if (tournament.getConclusionDate() != null)
            this.conclusionDate = tournament.getConclusionDate().format(formatter);
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

    public String getCreationDate() {
        return creationDate;
    }

    public User getStudent() {
        return student;
    }

    public void setStudent(User student) {
        this.student = student;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public String getAvailableDate() {
        return availableDate;
    }

    public void setAvailableDate(String availableDate) {
        this.availableDate = availableDate;
    }

    public String getConclusionDate() {
        return conclusionDate;
    }

    public void setConclusionDate(String conclusionDate) {
        this.conclusionDate = conclusionDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getNumberQuestions() {
        return numberQuestions;
    }

    public void setNumberQuestions(Integer numberQuestions) {
        this.numberQuestions = numberQuestions;
    }

    public LocalDateTime getCreationDateDate() {
        if (getCreationDate() == null || getCreationDate().isEmpty()) {
            return null;
        }
        return LocalDateTime.parse(getCreationDate(), formatter);
    }

    public LocalDateTime getAvailableDateDate() {
        if (getAvailableDate() == null || getAvailableDate().isEmpty()) {
            return null;
        }
        return LocalDateTime.parse(getAvailableDate(), formatter);
    }

    public LocalDateTime getConclusionDateDate() {
        if (getConclusionDate() == null || getConclusionDate().isEmpty()) {
            return null;
        }
        return LocalDateTime.parse(getConclusionDate(), formatter);
    }

    public Set<Topic> getTopics() {                //TO DO: check
        return topics;
    }

    public void setTopics(Set<Topic> topics) {                //TO DO: check
        this.topics = topics;
    }

    @Override
    public String toString() {
        return "TournamentDto{" +
                "id=" + id +
                "key=" + key +
                ", creationDate=" + creationDate + '\'' +
                ", availableDate='" + availableDate + '\'' +
                ", conclusionDate=" + conclusionDate + '\'' +
                ", number of questions= " + numberQuestions +
                ", title=" + title +
                ", key=" + key +
                ", formatter=" + formatter +
                '}';
    }
}
