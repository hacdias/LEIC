package pt.ulisboa.tecnico.socialsoftware.tutor.query.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler;
import pt.ulisboa.tecnico.socialsoftware.tutor.query.domain.AnswerQuery;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

public class AnswerQueryDto implements Serializable {
    private Integer id;
    private Integer queryId;
    private Integer answerQueryId;
    private String content;
    private String creationDate = null;
    private String byName;
    private String byUsername;
    private List<AnswerQueryDto> answers;

    public AnswerQueryDto() {
    }

    public AnswerQueryDto(AnswerQuery answerQuery) {
        setId(answerQuery.getId());

        if (answerQuery.getQuery() != null) { setQueryId(answerQuery.getQuery().getId()); }

        if (answerQuery.getAnswerQuery() != null) { setAnswerQueryId(answerQuery.getAnswerQuery().getId()); }

        setContent(answerQuery.getContent());
        setByName(answerQuery.getUser().getName());
        setByUsername(answerQuery.getUser().getUsername());

        if (answerQuery.getCreationDate() != null)
            setCreationDate(DateHandler.toISOString(answerQuery.getCreationDate()));

        if (answerQuery.getAnswers() != null)
            setAnswers(answerQuery.getAnswers().stream().map(AnswerQueryDto::new).collect(Collectors.toList()));
    }

    public Integer getId() { return id; }

    public void setId(Integer id) { this.id = id; }

    public Integer getQueryId() { return queryId; }

    public void setQueryId(Integer queryId) { this.queryId = queryId; }

    public Integer getAnswerQueryId() { return answerQueryId; }

    public void setAnswerQueryId(Integer answerQueryId) { this.answerQueryId = answerQueryId; }

    public String getContent() { return content; }

    public void setContent(String content) { this.content = content; }

    public String getCreationDate() { return creationDate; }

    public void setCreationDate(String creationDate) { this.creationDate = creationDate; }

    public String getByName() { return byName; }

    public void setByName(String byName) { this.byName = byName; }

    public String getByUsername() { return byUsername; }

    public void setByUsername(String byUsername) { this.byUsername = byUsername; }

    public List<AnswerQueryDto> getAnswers() { return answers; }

    public void setAnswers(List<AnswerQueryDto> answers) { this.answers = answers; }

    @Override
    public String toString() {
        return "AnswerQueryDto{" +
                "id=" + id +
                ", queryId=" + queryId +
                ", answerQueryId=" + answerQueryId +
                ", content='" + content + '\'' +
                ", creationDate='" + creationDate + '\'' +
                ", byName='" + byName + '\'' +
                ", byUsername='" + byUsername + '\'' +
                ", answers=" + answers +
                '}';
    }
}
