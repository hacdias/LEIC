package pt.ulisboa.tecnico.socialsoftware.tutor.query.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.query.domain.AnswerQuery;

@Repository
@Transactional
public interface AnswerQueryRepository extends JpaRepository<AnswerQuery, Integer> {}
