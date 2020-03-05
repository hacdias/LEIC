package pt.ulisboa.tecnico.socialsoftware.tutor.Tournament.dto;

//------------External Imports------------
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// ------------Internal Imports------------
import org.springframework.data.annotation.Transient;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.dto.QuizDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.StudentDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.Tournament.domain.Tournament;


public class TournamentDto implements Serializable {

    private Integer id;
    private String name;
    private Integer key;
    private String quizType;
    private Boolean created;
    private Integer numberQuestions;
    private String CreationTime = null;
    private String ConclusionTime = null;

    @Transient
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

    public TournamentDto() {
    }

    public TournamentDto(Tournament tournament) {
        this.id = tournament.getId();
        this.key = tournament.getKey();
        this.name = tournament.getName();
        this.created = tournament.getCreated();             //check later
        this.quizType = tournament.getquizType();
        this.numberQuestions = tournament.getNumberQuestions();

        
        if (tournament.getCreationTime() != null)
            this.creationTime = tournament.getCreationTime().format(formatter);

        if (tournament.getConclusionDate() != null)
            this.conclusionTime = tournament.getConclusionTime().format(formatter);

    }

    public Integer getId() {
        return id;
    }

    public String getName(){
        return name;
    }

    public Boolean getCreated() {                  //check later
        return created;
    }

    public Integer getNumberQuestions(){
        return numberQuestions;
    }

    public String getquizType(){
        return quizType;
    }


    public Integer getKey() {
        return key;
    }

    public LocalDateTime getCreationTime() {
        if (getCreationTime() == null || getCreationTime().isEmpty()) {
            return null;
        }
        return LocalDateTime.parse(getCreationTime(), TournamentDto.formatter);
    }

    public LocalDateTime getConclusionTime() {
        if (getConclusionTime() == null || getConclusionTime().isEmpty()) {
            return null;
        }
        return LocalDateTime.parse(getConclusionTime(), formatter);
    }



    @Override
    public String toString() {
        return "TournamentDto{" +
                "id=" + id +
                ", creation time=" + CreationTime +
                ", conclusion time=" + ConclusionTime +
                ", number of questions= " + numberQuestions +
                ", type=" + quizType +
                ", name=" + name +
                ", key=" + key +
                ", created=" + created +
                '}';
    }
}
