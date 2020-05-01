package pt.ulisboa.tecnico.socialsoftware.tutor.suggestions.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.suggestions.dto.SuggestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.SUGGESTION_ALREADY_APPROVED;

@Entity
@Table(name = "suggestions")
public class Suggestion {
    public enum Status {
        APPROVED, REJECTED, PENDING;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User student;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "question_id")
    private Question question;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "suggestion", fetch= FetchType.LAZY, orphanRemoval = true)
    private Set<SuggestionReview> suggestionReviews = new HashSet<>();

    @Column(name = "status")
    private Status status;

    @Column(name = "creation_date")
    private LocalDateTime creationDate;

    public Suggestion () {
    }

    public Suggestion (User student, Course course, SuggestionDto suggestionDto) {
        this.id = suggestionDto.getId();
        this.status = Suggestion.Status.valueOf(suggestionDto.getStatus());
        this.student = student;
        this.question = new Question(course, suggestionDto.getQuestion());
        this.creationDate = LocalDateTime.parse(suggestionDto.getCreationDate(), Course.formatter);
        this.question.setSuggestion(this);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public User getStudent() {
        return student;
    }

    public void setStudent(User student) {
        this.student = student;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public Set<SuggestionReview> getSuggestionReviews() {
        return suggestionReviews;
    }

    public void addSuggestionReview(SuggestionReview suggestionReview) {
        this.suggestionReviews.add(suggestionReview);
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    private void canRemove() {
        if (getStatus() == Status.APPROVED) {
            throw new TutorException(SUGGESTION_ALREADY_APPROVED, getId());
        }
    }

    public void update(SuggestionDto suggestionDto) {
        setStatus(Status.valueOf(suggestionDto.getStatus()));
    }

    public void remove() {
        canRemove();
        getStudent().getSuggestions().remove(this);
        getQuestion().remove();
        student = null;
        question = null;
    }

    @Override
    public String toString() {
        return "Suggestion{" +
            "id=" + id +
            ", student=" + student +
            ", question=" + question +
            ", suggestionReviews=" + suggestionReviews +
            ", status=" + status +
            ", creationDate=" + creationDate +
            '}';
    }
}

