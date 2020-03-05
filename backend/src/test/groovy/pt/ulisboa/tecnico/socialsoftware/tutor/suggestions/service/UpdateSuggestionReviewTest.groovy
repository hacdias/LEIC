package groovy.pt.ulisboa.tecnico.socialsoftware.tutor.suggestions.service

import spock.lang.Specification

class UpdateSuggestionReviewTest extends Specification {
    def setup () {

    }

    def "change suggestion approval and dont change justification"() {
        // suggestion approval is updated and justification isn't
        expect: false
    }
    
    def "dont change suggestion approval and change justification"() {
        // justification is updated and suggestion approval isn't
        expect: false
    }

    def "change both suggestion approval and justification"() {
        // approval and justification are updated
        expect: false
    }
    
    def "change justification of another teachers review"() {
        // justification is updated
        expect: false
    }
}