package pt.ulisboa.tecnico.socialsoftware.tutor.query.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.query.domain.Query;

import java.io.Serializable;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class QueryDto implements Serializable {
    private Integer id;
    private Integer key;
    private String title;
    private String content;
    private List<AnswerQueryDto> answers = new ArrayList<>();
    private String creationDate = null;

    public QueryDto() {
    }

    public QueryDto(Query query) {
        this.id = query.getId();
        this.key = query.getKey();
        this.title = query.getTitle();
        this.content = query.getContent();
        this.answers = query.getAnswers().stream().map(AnswerQueryDto::new).collect(Collectors.toList());

        if (query.getCreationDate() != null)
            this.creationDate = query.getCreationDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

    public Integer getId() { return id; }

    public void setId(Integer id) { this.id = id; }

    public Integer getKey() { return key; }

    public void setKey(Integer key) { this.key = key; }

    public String getTitle() { return title; }

    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }

    public void setContent(String content) { this.content = content; }

    public List<AnswerQueryDto> getAnswers() { return answers; }

    public void setAnswers(List<AnswerQueryDto> answers) { this.answers = answers; }

    public String getCreationDate() { return creationDate; }

    public void setCreationDate(String creationDate) { this.creationDate = creationDate; }

    @Override
    public String toString() {
        return "QueryDto{" +
                "id=" + id +
                ", key=" + key +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", answers=" + answers +
                ", creationDate='" + creationDate + '\'' +
                '}';
    }
}
