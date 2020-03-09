package pt.ulisboa.tecnico.socialsoftware.tutor.suggestions.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.suggestions.domain.Suggestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.StudentDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto;

import java.io.Serializable;

public class SuggestionDto implements Serializable {
    private Integer id;
    private Integer key;
    private Boolean approved;
    private UserDto student;

    public SuggestionDto () {
    }

    public SuggestionDto (Suggestion suggestion) {
        this.id = suggestion.getId();
        this.key = suggestion.getKey();
        this.approved = suggestion.getApproved();
        this.student = new UserDto(suggestion.getStudent());
    }

    public Integer getId() {
        return id;
    }

    public Integer getKey() {
        return key;
    }

    public Boolean getApproved() {
        return approved;
    }

    public UserDto getStudent() {
        return student;
    }

    @Override
    public String toString() {
        return "SuggestionDto{" +
                "id=" + id +
                ", key=" + key +
                ", approved=" + approved +
                ", student=" + student +
                '}';
    }
}
