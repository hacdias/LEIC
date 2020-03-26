package pt.ulisboa.tecnico.socialsoftware.tutor.suggestions.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.suggestions.domain.SuggestionReview;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto;

import java.io.Serializable;
import java.time.format.DateTimeFormatter;

public class SuggestionReviewDto implements Serializable {
    private Integer id;
    private Boolean approved;
    private String justification;
    private String creationDate;

    public SuggestionReviewDto () {
    }

    public SuggestionReviewDto (SuggestionReview suggestionReview) {
        this.id = suggestionReview.getId();
        this.approved = suggestionReview.getApproved();
        this.justification = suggestionReview.getJustification();

        if (suggestionReview.getCreationDate() != null) {
            this.creationDate = suggestionReview.getCreationDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        }
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Boolean getApproved() {
        return approved;
    }

    public void setApproved(Boolean approved) {
        this.approved = approved;
    }

    public String getJustification() { return justification; }

    public void setJustification(String justification) { this.justification = justification; }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    @Override
    public String toString() {
        return "SuggestionDto{" +
                "id=" + id +
                ", approved=" + approved +
                ", justification=" + justification +
                ", creationDate='" + creationDate + '\'' +
                '}';
    }
}