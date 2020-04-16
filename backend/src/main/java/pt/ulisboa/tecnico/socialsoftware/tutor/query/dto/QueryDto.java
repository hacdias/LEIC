package pt.ulisboa.tecnico.socialsoftware.tutor.query.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.query.domain.Query;

import java.io.Serializable;
import java.time.format.DateTimeFormatter;


public class QueryDto implements Serializable {
    private Integer id;
    private String title;
    private String content;
    private String creationDate = null;
    private Integer numberAnswers;
    private Integer questionId;
    private String byUsername;
    private String byName;

    public QueryDto() {
    }

    public QueryDto(Query query) {
        this.id = query.getId();
        this.title = query.getTitle();
        this.content = query.getContent();
        this.numberAnswers = query.getAnswers().size();
        this.questionId = query.getQuestion().getId();
        this.byUsername = query.getStudent().getUsername();
        this.byName = query.getStudent().getName();

        if (query.getCreationDate() != null)
            this.creationDate = query.getCreationDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

    public Integer getId() { return id; }

    public void setId(Integer id) { this.id = id; }

    public String getTitle() { return title; }

    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }

    public void setContent(String content) { this.content = content; }

    public String getCreationDate() { return creationDate; }

    public void setCreationDate(String creationDate) { this.creationDate = creationDate; }

    public Integer getNumberAnswers() { return numberAnswers; }

    public Integer getQuestionId() { return questionId; }

    public void setQuestionId(Integer questionId) { this.questionId = questionId; }

    public String getbyName() { return this.byName; }

    public void setbyUsername(String byUsername) { this.byUsername = byUsername; }

    public String getbyUsername() { return this.byUsername; }

    public void setbyName(String byName) { this.byName = byName; }

    @Override
    public String toString() {
        return "QueryDto{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", creationDate='" + creationDate + '\'' +
                '}';
    }
}
