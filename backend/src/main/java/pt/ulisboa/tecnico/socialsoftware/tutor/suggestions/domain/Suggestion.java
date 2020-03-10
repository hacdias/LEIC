package pt.ulisboa.tecnico.socialsoftware.tutor.suggestions.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.suggestions.dto.SuggestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto;

import javax.persistence.*;
import java.time.LocalDateTime;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.QUESTION_IS_USED_IN_QUIZ;
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.SUGGESTION_ALREADY_APPROVED;

@Entity
@Table(name = "suggestions")
public class Suggestion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, nullable = false)
    private Integer key;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User student;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "question_id")
    private Question question;

    @Column(name = "approved")
    private Boolean approved;

    @Column(name = "creation_date")
    private LocalDateTime creationDate;

    public Suggestion () {
    }

    public Suggestion (User student, Question question, SuggestionDto suggestionDto) {
        this.id = suggestionDto.getId();
        this.key = suggestionDto.getKey();
        this.approved = suggestionDto.getApproved();
        this.student = student;
        this.question = question;
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

    private void canRemove() {
        if (getApproved()) {
            throw new TutorException(SUGGESTION_ALREADY_APPROVED, getId());
        }
    }

    public void update(SuggestionDto suggestionDto) {
        setApproved(suggestionDto.getApproved());
    }

    public void remove() {
        canRemove();
        getStudent().getSuggestions().remove(this);
        getQuestion().remove();
        student = null;
        question = null;
    }
}

