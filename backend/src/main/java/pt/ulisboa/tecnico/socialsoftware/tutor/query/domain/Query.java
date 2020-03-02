package pt.ulisboa.tecnico.socialsoftware.tutor.query.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import javax.persistence.*;

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

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User student;

    @OneToOne
    @JoinColumn(name = "question_id")
    private Question question;
}
