package pt.ulisboa.tecnico.socialsoftware.tutor.query.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler;
import pt.ulisboa.tecnico.socialsoftware.tutor.query.domain.Query;

import java.io.Serializable;

public class QueryDto implements Serializable {
    private Integer id;
    private String title;
    private String content;
    private String creationDate = null;
    private Integer numberAnswers;
    private Integer questionId;
    private String byUsername;
    private String byName;
    private Boolean shared;

    public QueryDto() {
    }

    public QueryDto(Query query) {
        setId(query.getId());
        setTitle(query.getTitle());
        setContent(query.getContent());
        setNumberAnswers(query.getAnswers().size());
        setQuestionId(query.getQuestion().getId());
        setByUsername(query.getStudent().getUsername());
        setByName(query.getStudent().getName());

        if (query.getShared() != null) { setShared(query.getShared()); }
        else { setShared(false); }

        if (query.getCreationDate() != null)
            setCreationDate(DateHandler.toISOString(query.getCreationDate()));
    }

    public Integer getId() { return id; }

    public void setId(Integer id) { this.id = id; }

    public String getTitle() { return title; }

    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }

    public void setContent(String content) { this.content = content; }

    public Boolean getShared() { return shared; }

    public void setShared(Boolean shared) { this.shared = shared; }

    public String getCreationDate() { return creationDate; }

    public void setCreationDate(String creationDate) { this.creationDate = creationDate; }

    public Integer getNumberAnswers() { return numberAnswers; }

    public void setNumberAnswers(Integer numberAnswers) {
        this.numberAnswers = numberAnswers;
    }

    public Integer getQuestionId() { return questionId; }

    public void setQuestionId(Integer questionId) { this.questionId = questionId; }

    public String getByName() { return byName; }

    public void setByUsername(String byUsername) { this.byUsername = byUsername; }

    public String getByUsername() { return byUsername; }

    public void setByName(String byName) { this.byName = byName; }

    @Override
    public String toString() {
        return "QueryDto{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", creationDate='" + creationDate + '\'' +
                ", numberAnswers=" + numberAnswers +
                ", questionId=" + questionId +
                ", byUsername='" + byUsername + '\'' +
                ", byName='" + byName + '\'' +
                ", shared=" + shared +
                '}';
    }
}
