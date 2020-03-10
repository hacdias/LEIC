package pt.ulisboa.tecnico.socialsoftware.tutor.query.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.query.dto.QueryDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "queries",
        indexes = {
                @Index(name = "query_indx_0", columnList = "key")
        })

public class Query {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique=true, nullable = false)
    private Integer key;

    @Column(columnDefinition = "TEXT")
    private String content;

    private String title;

    @Column(name = "creation_date")
    private LocalDateTime creationDate;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User student;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "question_id")
    private Question question;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "query", fetch = FetchType.EAGER, orphanRemoval=true)
    private List<AnswerQuery> answers = new ArrayList<>();

    public Query() {
    }

    public Query(Question question, User student, QueryDto queryDto) {
        this.title = queryDto.getTitle();
        this.key = queryDto.getKey();
        this.content = queryDto.getContent();

        this.question = question;
        question.addQuery(this);
        this.student = student;
        student.addQuery(this);
    }

    public Integer getId() { return id; }

    public void setId(Integer id) { this.id = id;}

    public Integer getKey() { return key;}

    public void setKey(Integer key) { this.key = key;}

    public String getContent() { return content;}

    public void setContent(String content) { this.content = content;}

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
        setTitle(queryDto.getTitle());
        setContent(queryDto.getContent());
    }

    public void remove() {
        getQuestion().getQueries().remove(this);
        question = null;
        getStudent().getQueries().remove(this);
        student = null;
    }
}
