package pt.ulisboa.tecnico.socialsoftware.tutor.suggestions.service

import spock.lang.Specification

class CreateSuggestionReviewTest extends Specification {
    def setup () {

    }

    def "suggestion is approved with no justification"() {
        // suggestion review is created with no justification
        expect: false
    }
    
    def "suggestion is rejected with justification"() {
        // suggestion review is created with justification
        expect: false
    }
    
    def "suggestion review approval is empty"() {
        // an exception is thrown
        expect: false
    }
    
    def "suggestion review teacher is empty"() {
        // an exception is thrown
        expect: false
    }

    def "create suggestion review to already reviewed suggestion"() {
        // an exception is thrown
        expect: false
    }
}