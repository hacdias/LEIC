package pt.ulisboa.tecnico.socialsoftware.tutor.query.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.query.domain.Query;

@Repository
@Transactional
public interface QueryRepository extends JpaRepository<Query, Integer> {
    @org.springframework.data.jpa.repository.Query
            (value = "SELECT MAX(key) FROM queries", nativeQuery = true)
    Integer getMaxQueryNumber();
}
