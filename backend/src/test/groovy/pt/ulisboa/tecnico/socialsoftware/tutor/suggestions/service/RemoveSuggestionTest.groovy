package groovy.pt.ulisboa.tecnico.socialsoftware.tutor.suggestions.service

import spock.lang.Specification

class RemoveSuggestionTest extends Specification {
    def setup() {

    }

    def "remove a suggestion for an unapproved question"() {
        expect: false

    }

    def "remove a suggestion for an approved question" () {
        // NOTE: should fail since the question was already approved by a teacher
        // and cannot be modified anymore by the student.
        expect: false
    }
}
