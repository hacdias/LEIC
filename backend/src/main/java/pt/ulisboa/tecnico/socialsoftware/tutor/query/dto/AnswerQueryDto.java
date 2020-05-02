package pt.ulisboa.tecnico.socialsoftware.tutor.query.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler;
import pt.ulisboa.tecnico.socialsoftware.tutor.query.domain.AnswerQuery;

import java.io.Serializable;

public class AnswerQueryDto implements Serializable {
    private Integer id;
    private String content;
    private String creationDate = null;
    private String byName;
    private String byUsername;

    public AnswerQueryDto() {
    }

    public AnswerQueryDto(AnswerQuery answerQuery) {
        setId(answerQuery.getId());
        setContent(answerQuery.getContent());
        setByName(answerQuery.getTeacher().getName());
        setByUsername(answerQuery.getTeacher().getUsername());

        if (answerQuery.getCreationDate() != null)
            setCreationDate(DateHandler.toISOString(answerQuery.getCreationDate()));
    }

    public Integer getId() { return id; }

    public void setId(Integer id) { this.id = id; }

    public String getContent() { return content; }

    public void setContent(String content) { this.content = content; }

    public String getCreationDate() { return creationDate; }

    public void setCreationDate(String creationDate) { this.creationDate = creationDate; }

    public String getByName() { return byName; }

    public void setByName(String byName) { this.byName = byName; }

    public String getByUsername() { return byUsername; }

    public void setByUsername(String byUsername) { this.byUsername = byUsername; }

    @Override
    public String toString() {
        return "AnswerQueryDto{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", creationDate='" + creationDate + '\'' +
                ", byName='" + byName + '\'' +
                ", byUsername='" + byUsername + '\'' +
                '}';
    }
}
