package groovy.pt.ulisboa.tecnico.socialsoftware.tutor.suggestions.service

import spock.lang.Specification

class RemoveSuggestionReviewTest extends Specification {
    def setup () {

    }

    def "remove approved suggestion review"() {
        // suggestion review is deleted
        expect: false
    }
    
    def "remove rejected suggestion review"() {
        // suggestion review is deleted
        expect: false
    }
    
    def "remove suggestion with justification empty"() {
        // suggestion review is deleted
        expect: false
    }
}