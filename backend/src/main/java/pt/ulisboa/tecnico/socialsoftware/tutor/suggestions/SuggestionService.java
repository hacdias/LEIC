package pt.ulisboa.tecnico.socialsoftware.tutor.suggestions;

import org.springframework.beans.factory.annotation.Autowired;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService;
import pt.ulisboa.tecnico.socialsoftware.tutor.suggestions.domain.Suggestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.suggestions.dto.SuggestionDto;

public class SuggestionService {

    @Autowired
    private QuestionService questionService;


    public SuggestionDto createSuggestion(SuggestionDto suggestionDto) {
        // TODO
        return null;
    }

    public SuggestionDto updateSuggestion(Integer suggestionid, SuggestionDto suggestionDto) {
        // TODO
        return null;
    }

    public void removeSuggestion(Integer suggestionId) {
        // TODO
    }
}
