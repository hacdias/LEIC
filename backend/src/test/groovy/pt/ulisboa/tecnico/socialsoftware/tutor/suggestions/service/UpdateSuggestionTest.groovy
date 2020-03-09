package groovy.pt.ulisboa.tecnico.socialsoftware.tutor.suggestions.service

import spock.lang.Specification

class UpdateSuggestionTest extends Specification {
    def setup() {

    }

    def "update suggestion with new title"() {
        expect: false
    }

    def "update suggestion with new correct answer"() {
        expect: false
    }

    def "update suggestion with new content"() {
        expect: false
    }

    def "update suggestion for approved question"() {
        // NOTE: should fail since the question was already approved by a teacher
        // and should not be modified by the student anymore.
        expect: false
    }
}
