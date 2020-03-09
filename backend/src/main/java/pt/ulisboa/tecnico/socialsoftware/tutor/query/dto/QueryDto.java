package pt.ulisboa.tecnico.socialsoftware.tutor.query.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.query.domain.Query;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.StudentDto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class QueryDto implements Serializable {
    private Integer id;
    private Integer key;
    private String title;
    private String content;
    private StudentDto student;
    private QuestionDto question;
    private List<AnswerQueryDto> answers = new ArrayList<>();
    private String creationDate = null;

    public QueryDto() {
    }

    public QueryDto(Query query) {
    }

    public Integer getId() { return id; }

    public void setId(Integer id) { this.id = id; }

    public Integer getKey() { return key; }

    public void setKey(Integer key) { this.key = key; }

    public String getTitle() { return title; }

    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }

    public void setContent(String content) { this.content = content; }

    public StudentDto getStudent() { return student; }

    public void setStudent(StudentDto student) { this.student = student; }

    public QuestionDto getQuestion() { return question; }

    public void setQuestion(QuestionDto question) { this.question = question; }

    public List<AnswerQueryDto> getAnswers() { return answers; }

    public void setAnswers(List<AnswerQueryDto> answers) { this.answers = answers; }

    public String getCreationDate() { return creationDate; }

    public void setCreationDate(String creationDate) { this.creationDate = creationDate; }
}
