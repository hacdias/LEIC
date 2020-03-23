package pt.ulisboa.tecnico.socialsoftware.tutor.suggestions.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.suggestions.domain.Suggestion;

import java.io.Serializable;
import java.time.format.DateTimeFormatter;

public class SuggestionDto implements Serializable {
    private Integer id;
    private Integer key;
    private Boolean approved;
    private String creationDate;
    private QuestionDto questionDto;

    public SuggestionDto () {
    }

    public SuggestionDto (Suggestion suggestion) {
        this.id = suggestion.getId();
        this.key = suggestion.getKey();
        this.approved = suggestion.getApproved();
        this.questionDto = new QuestionDto(suggestion.getQuestion());

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

    public Integer getKey() {
        return key;
    }

    public void setKey(Integer key) {
        this.key = key;
    }

    public Boolean getApproved() {
        return approved;
    }

    public void setApproved(Boolean approved) {
        this.approved = approved;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public QuestionDto getQuestionDto() {
        return questionDto;
    }

    public void setQuestionDto(QuestionDto questionDto) {
        this.questionDto = questionDto;
    }

    @Override
    public String toString() {
        return "SuggestionDto{" +
                "id=" + id +
                ", key=" + key +
                ", approved=" + approved +
                ", creationDate='" + creationDate + '\'' +
                ", questionDto=" + questionDto +
                '}';
    }
}
