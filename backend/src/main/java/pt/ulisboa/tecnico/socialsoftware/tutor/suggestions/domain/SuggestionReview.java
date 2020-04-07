package pt.ulisboa.tecnico.socialsoftware.tutor.suggestions.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.suggestions.dto.SuggestionReviewDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "suggestion_reviews")
public class SuggestionReview {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User teacher;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "suggestion_id")
    private Suggestion suggestion;

    @Column(name = "justification", columnDefinition = "TEXT")
    private String justification;

    @Column(name = "approved")
    private Boolean approved;

    @Column(name = "creation_date")
    private LocalDateTime creationDate;

    public SuggestionReview() {
    }

    public SuggestionReview(User teacher, Suggestion suggestion, SuggestionReviewDto suggestionReviewDto) {
        this.id = suggestionReviewDto.getId();
        this.approved = suggestionReviewDto.getApproved();
        this.justification = suggestionReviewDto.getJustification();
        this.teacher = teacher;
        this.suggestion = suggestion;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) { this.id = id; }

    public Suggestion getSuggestion() {
        return suggestion;
    }

    public void setSuggestion(Suggestion suggestion) {
        this.suggestion = suggestion;
    }

    public User getTeacher() {
        return teacher;
    }

    public void setTeacher(User teacher) {
        this.teacher = teacher;
    }

    public String getJustification() {
        return justification;
    }

    public void setJustification(String justification) {
        this.justification = justification;
    }

    public Boolean getApproved() {
        return approved;
    }

    public void setApproved(Boolean approved) {
        this.approved = approved;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public void remove() {
        getTeacher().getSuggestionReviews().remove(this);
        teacher = null;
        suggestion = null;
    }
}