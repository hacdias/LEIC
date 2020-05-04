package pt.ulisboa.tecnico.socialsoftware.tutor.query.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.query.dto.AnswerQueryDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

@Entity
@Table(name = "answer_query")
public class AnswerQuery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(columnDefinition = "TEXT")
    private String content;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "query_id")
    private Query query;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "answer_query_id")
    private AnswerQuery answerQuery;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "creation_date")
    private LocalDateTime creationDate;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "answer_query", fetch = FetchType.EAGER, orphanRemoval=true)
    private List<AnswerQuery> answers = new ArrayList<>();

    public AnswerQuery(){}

    public AnswerQuery(Query query, User user, AnswerQueryDto answerQueryDto) {
        setContent(answerQueryDto.getContent());
        setQuery(query);
        query.addAnswer(this);
        setUser(user);
        user.addAnswer(this);
        setCreationDate(DateHandler.toLocalDateTime(answerQueryDto.getCreationDate()));
    }

    public AnswerQuery(AnswerQuery answerQuery, User user, AnswerQueryDto answerQueryDto) {
        setContent(answerQueryDto.getContent());
        setAnswerQuery(answerQuery);
        answerQuery.addAnswer(this);
        setUser(user);
        user.addAnswer(this);
        setCreationDate(DateHandler.toLocalDateTime(answerQueryDto.getCreationDate()));
    }

    public String getContent() { return content; }

    public void setContent(String content) { this.content = content; }

    public Integer getId() { return id; }

    public void setId(Integer id) { this.id = id; }

    public Query getQuery() { return query; }

    public void setQuery(Query query) { this.query = query; }

    public AnswerQuery getAnswerQuery() { return answerQuery; }

    public void setAnswerQuery(AnswerQuery answerQuery) { this.answerQuery = answerQuery; }

    public User getUser() { return user; }

    public void setUser(User teacher) { this.user = teacher; }

    public LocalDateTime getCreationDate() { return creationDate; }

    public void setCreationDate(LocalDateTime creationDate) { this.creationDate = creationDate; }

    public List<AnswerQuery> getAnswers() { return answers; }

    public void setAnswers(List<AnswerQuery> answers) { this.answers = answers; }

    public void addAnswer(AnswerQuery answer) {this.answers.add(answer); }

    public void update(AnswerQueryDto answerQueryDto) {
        checkAnswerQueryConsistent(answerQueryDto);
        setContent(answerQueryDto.getContent());
    }

    private void checkAnswerQueryConsistent(AnswerQueryDto answerQueryDto) {

        if (answerQueryDto.getContent() == null ||
                answerQueryDto.getContent().trim().length() == 0) {
            throw new TutorException(ANSWER_QUERY_MISSING_DATA);
        }
    }

    public void remove() {
        canRemove();
        getQuery().getAnswers().remove(this);
        query = null;
        getUser().getQueryAnswers().remove(this);
        user = null;
    }

    private void canRemove() { /* for future feature */  }

    @Override
    public String toString() {
        return "AnswerQuery{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", query=" + query.getId() +
                ", answerQuery=" + answerQuery +
                ", user=" + user +
                ", creationDate=" + creationDate +
                ", answers=" + answers +
                '}';
    }

}
