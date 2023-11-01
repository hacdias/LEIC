package pt.ulisboa.tecnico.socialsoftware.tutor.suggestions.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.suggestions.domain.SuggestionReview;

@Repository
@Transactional
public interface SuggestionReviewRepository extends JpaRepository<SuggestionReview, Integer> {

}