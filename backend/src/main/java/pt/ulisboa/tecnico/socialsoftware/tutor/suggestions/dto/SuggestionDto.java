package pt.ulisboa.tecnico.socialsoftware.tutor.suggestions.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.suggestions.domain.Suggestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto;

import java.io.Serializable;

public class SuggestionDto implements Serializable {
    private Integer id;
    private Integer key;
    private Boolean approved;
    private UserDto student;
    private QuestionDto question;

    public SuggestionDto () {
    }

    public SuggestionDto (Suggestion suggestion) {
        this.id = suggestion.getId();
        this.key = suggestion.getKey();
        this.approved = suggestion.getApproved();
        this.student = new UserDto(suggestion.getStudent());
        this.question = new QuestionDto(suggestion.getQuestion());
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

    public UserDto getStudent() {
        return student;
    }

    public void setStudent(UserDto student) {
        this.student = student;
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
                ", key=" + key +
                ", approved=" + approved +
                ", student=" + student +
                ", question=" + question +
                '}';
    }
}
