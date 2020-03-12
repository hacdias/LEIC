package pt.ulisboa.tecnico.socialsoftware.tutor.query.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.query.dto.AnswerQueryDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

@Entity
@Table(name = "answer_query")
public class AnswerQuery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique=true, nullable = false)
    private Integer key;

    @Column(columnDefinition = "TEXT")
    private String content;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "query_id")
    private Query query;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User teacher;

    @Column(name = "creation_date")
    private LocalDateTime creationDate;

    public AnswerQuery(){}

    public AnswerQuery(Query query, User teacher, AnswerQueryDto answerQuery) {
        this.key = answerQuery.getKey();
        this.content = answerQuery.getContent();
        this.query = query;
        query.addAnswer(this);
        this.teacher = teacher;
        teacher.addAnswer(this);
    }

    public String getContent() { return content; }

    public void setContent(String content) { this.content = content; }

    public Integer getId() { return id; }

    public void setKey(Integer key) { this.key = key; }

    public Integer getKey() { return key; }

    public void setId(Integer id) { this.id = id; }

    public Query getQuery() { return query; }

    public void setQuery(Query query) { this.query = query; }

    public User getTeacher() { return teacher; }

    public void setTeacher(User teacher) { this.teacher = teacher; }

    public LocalDateTime getCreationDate() { return creationDate; }

    public void setCreationDate(LocalDateTime creationDate) { this.creationDate = creationDate; }

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
        getTeacher().getQueryAnswers().remove(this);
        teacher = null;
    }

    private void canRemove() { /* for future feature */  }

}
