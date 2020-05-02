package pt.ulisboa.tecnico.socialsoftware.tutor.query.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.query.dto.QueryDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

@Entity
@Table(name = "queries")
public class Query {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(columnDefinition = "TEXT")
    private String content;

    private String title;

    @Column(name = "shared")
    private Boolean shared;

    @Column(name = "creation_date")
    private LocalDateTime creationDate;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User student;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "question_id")
    private Question question;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "question_answer_id")
    private QuestionAnswer question_answer;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "query", fetch = FetchType.EAGER, orphanRemoval=true)
    private List<AnswerQuery> answers = new ArrayList<>();

    public Query() {
    }

    public Query(Question question, User student, QuestionAnswer question_answer, QueryDto queryDto) {
        checkQueryConsistent(queryDto);

        setTitle(queryDto.getTitle());
        setContent(queryDto.getContent());

        setQuestion(question);
        question.addQuery(this);
        setStudent(student);
        student.addQuery(this);
        setQuestionAnswer(question_answer);
        question_answer.addQuery(this);
        if (queryDto.getShared() != null) { setShared(queryDto.getShared()); }
        else { setShared(false); }

        setCreationDate(DateHandler.toLocalDateTime(queryDto.getCreationDate()));
    }

    public Integer getId() { return id; }

    public void setId(Integer id) { this.id = id;}

    public String getContent() { return content;}

    public void setContent(String content) { this.content = content;}

    public Boolean getShared() { return shared; }

    public void setShared(Boolean shared) { this.shared = shared; }

    public QuestionAnswer getQuestionAnswer() { return question_answer; }

    public void setQuestionAnswer(QuestionAnswer question_answer) { this.question_answer = question_answer; }

    public String getTitle() { return title;}

    public void setTitle(String title) { this.title = title;}

    public LocalDateTime getCreationDate() { return creationDate;}

    public void setCreationDate(LocalDateTime creationDate) { this.creationDate = creationDate;}

    public User getStudent() { return student;}

    public void setStudent(User student) { this.student = student;}

    public Question getQuestion() { return question;}

    public void setQuestion(Question question) { this.question = question;}

    public List<AnswerQuery> getAnswers() { return answers;}

    public void setAnswers(List<AnswerQuery> answers) { this.answers = answers;}

    public void addAnswer(AnswerQuery answer) {
        answers.add(answer);
    }

    public void update(QueryDto queryDto) {
        checkQueryConsistent(queryDto);

        setTitle(queryDto.getTitle());
        setContent(queryDto.getContent());
        if (queryDto.getShared() != null) { setShared(queryDto.getShared()); }
        else { setShared(false); }
    }

    public void remove() {
        canRemove();

        getQuestion().getQueries().remove(this);
        question = null;
        getStudent().getQueries().remove(this);
        student = null;
    }

    private void canRemove() {
        if (!getAnswers().isEmpty()) {
            throw new TutorException(QUERY_IS_ANSWERED, id);
        }
    }

    private void checkQueryConsistent(QueryDto queryDto) {

        if (queryDto.getTitle() == null || queryDto.getContent() == null ||
                queryDto.getTitle().trim().length() == 0 ||
                queryDto.getContent().trim().length() == 0) {
            throw new TutorException(QUERY_MISSING_DATA);
        }
    }

    @Override
    public String toString() {
        return "Query{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", title='" + title + '\'' +
                ", shared=" + shared +
                ", creationDate=" + creationDate +
                ", student=" + student.getUsername() +
                ", question=" + question +
                ", question_answer=" + question_answer +
                ", answers=" + answers +
                '}';
    }
}
