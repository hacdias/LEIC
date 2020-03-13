package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.repository;

//--------External Imports--------
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

//--------Internal Imports--------
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.Tournament;


@Repository
@Transactional
public interface TournamentRepository extends JpaRepository<Tournament, Integer> {  
    
    @Query(value = "SELECT MAX(key) FROM tournaments", nativeQuery = true)
    Integer getMaxTournamentKey();
}