package pt.ulisboa.tecnico.socialsoftware.tutor.suggestions.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.suggestions.dto.SuggestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.suggestions.domain.SuggestionReview;

import java.io.Serializable;
import java.time.format.DateTimeFormatter;

public class SuggestionReviewDto implements Serializable {
    private Integer id;
    private Boolean approved;
    private String justification;
    private String creationDate;
    private SuggestionDto suggestion;

    public SuggestionReviewDto () {
    }

    public SuggestionReviewDto (SuggestionReview suggestionReview) {
        this.id = suggestionReview.getId();
        this.approved = suggestionReview.getApproved();
        this.justification = "";
        this.suggestion = new SuggestionDto(suggestionReview.getSuggestion());

        if (suggestionReview.getJustification() != null) {
            this.justification = suggestionReview.getJustification();
        }

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

    public SuggestionDto getSuggestion() {
        return suggestion;
    }

    public void setSuggestion(SuggestionDto suggestion) {
        this.suggestion = suggestion;
    }

    @Override
    public String toString() {
        return "SuggestionReviewDto{" +
                "id=" + id +
                ", approved=" + approved +
                ", justification=" + justification +
                ", creationDate='" + creationDate + '\'' +
                ", suggestion=" + suggestion +
                '}';
    }
}