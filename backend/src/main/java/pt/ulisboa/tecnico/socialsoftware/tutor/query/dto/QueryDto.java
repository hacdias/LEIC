package pt.ulisboa.tecnico.socialsoftware.tutor.query.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.query.domain.Query;

import java.io.Serializable;
import java.time.format.DateTimeFormatter;


public class QueryDto implements Serializable {
    private Integer id;
    private String title;
    private String content;
    private String creationDate = null;

    public QueryDto() {
    }

    public QueryDto(Query query) {
        this.id = query.getId();
        this.title = query.getTitle();
        this.content = query.getContent();

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
