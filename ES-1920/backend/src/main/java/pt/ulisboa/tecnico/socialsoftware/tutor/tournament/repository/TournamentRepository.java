package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.repository;

//--------External Imports--------
import java.util.List;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

//--------Internal Imports--------
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.Tournament;


@Repository
@Transactional
public interface TournamentRepository extends JpaRepository<Tournament, Integer> {  
    
    @Query(value = "SELECT * FROM tournaments t, course_executions c WHERE c.id = t.course_execution_id AND c.id = :executionId", nativeQuery = true)
    List<Tournament> findTournaments(int executionId);

    @Query(value = "SELECT MAX(key) FROM tournaments", nativeQuery = true)
    Integer getMaxTournamentKey();
}