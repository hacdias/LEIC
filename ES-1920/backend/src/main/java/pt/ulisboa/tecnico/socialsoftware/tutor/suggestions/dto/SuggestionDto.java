package pt.ulisboa.tecnico.socialsoftware.tutor.suggestions.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.suggestions.domain.Suggestion;

import java.io.Serializable;
import java.time.format.DateTimeFormatter;

public class SuggestionDto implements Serializable {
    private Integer id;
    private String status;
    private String creationDate;
    private QuestionDto question;

    public SuggestionDto () {
    }

    public SuggestionDto (Suggestion suggestion) {
        this.id = suggestion.getId();
        this.status = suggestion.getStatus().name();
        this.question = new QuestionDto(suggestion.getQuestion());

        if (suggestion.getCreationDate() != null) {
            this.creationDate = suggestion.getCreationDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        }
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public QuestionDto getQuestion() {
        return question;
    }

    public void setQuestion(QuestionDto question) {
        this.question = question;
    }

    @Override
    public String toString() {
        return "SuggestionDto{" +
            "id=" + id +
            ", status='" + status + '\'' +
            ", creationDate='" + creationDate + '\'' +
            ", question=" + question +
            '}';
    }
}
