package pt.ulisboa.tecnico.socialsoftware.tutor.query.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import javax.persistence.*;

@Entity
@Table(name = "answer_query")
public class AnswerQuery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer sequence;

    @Column(columnDefinition = "TEXT")
    private String content;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "query_id")
    private Query query;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public AnswerQuery(){}
}
