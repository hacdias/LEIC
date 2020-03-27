package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto;

//------------External Imports------------
import java.util.Set;
import java.util.HashSet;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// ------------Internal Imports------------
import org.springframework.data.annotation.Transient;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.Tournament;


public class TournamentDto implements Serializable {

    private Integer id;
    private String creationDate = null;
    private String availableDate = null;
    private String conclusionDate = null;
    private String title;
    private Integer numberQuestions;
    private Set<TopicDto> topics = new HashSet<>();

    @Transient
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public TournamentDto() {}

    public TournamentDto(Tournament tournament) {
        this.id = tournament.getId();
        this.title = tournament.getTitle();
        this.numberQuestions = tournament.getNumberQuestions();

        if (tournament.getCreationDate() != null)
            this.creationDate = tournament.getCreationDate().format(formatter);
        if (tournament.getAvailableDate() != null)
            this.availableDate = tournament.getAvailableDate().format(formatter);
        if (tournament.getConclusionDate() != null)
            this.conclusionDate = tournament.getConclusionDate().format(formatter);

        if (tournament.getTopics() != null) {
            for (Topic topic: tournament.getTopics()) {
                TopicDto topicDto = new TopicDto(topic);
                addTopic(topicDto);
            }
        } 
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public String getCreationDate() {
        return creationDate;
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

    public Set<TopicDto> getTopics() {         
        return topics;
    }

    public void setTopics(Set<TopicDto> topics) {           
        this.topics = topics;
    }

    public void addTopic(TopicDto topic) {
        this.topics.add(topic);
    }

    @Override
    public String toString() {
        return "TournamentDto{" +
                "id=" + id +
                ", creationDate=" + creationDate + '\'' +
                ", availableDate='" + availableDate + '\'' +
                ", conclusionDate=" + conclusionDate + '\'' +
                ", title=" + title +
                '}';
    }
}
