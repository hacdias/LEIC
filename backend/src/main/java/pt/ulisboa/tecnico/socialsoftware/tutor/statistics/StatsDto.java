package pt.ulisboa.tecnico.socialsoftware.tutor.statistics;

import java.io.Serializable;

public class StatsDto implements Serializable {
    private Integer totalQuizzes = 0;
    private Integer totalAnswers = 0;
    private Integer totalUniqueQuestions = 0;
    private float correctAnswers = 0;
    private float improvedCorrectAnswers = 0;
    private Integer uniqueCorrectAnswers = 0;
    private Integer uniqueWrongAnswers = 0;
    private Integer totalAvailableQuestions = 0;
    private Integer totalProposedSuggestions = 0;
    private Integer approvedProposedSuggestions = 0;
    private Integer totalQueriesSubmitted = 0;
    private Integer sharedQueries = 0;

    private Boolean privateSuggestionStats = false;

    public Boolean getPrivateSuggestionStats() {
        return privateSuggestionStats;
    }

    public void setPrivateSuggestionStats(Boolean privateSuggestionStats) {
        this.privateSuggestionStats = privateSuggestionStats;
    }

    public Integer getTotalQuizzes() {
        return totalQuizzes;
    }

    public void setTotalQuizzes(Integer totalQuizzes) {
        this.totalQuizzes = totalQuizzes;
    }

    public Integer getTotalAnswers() {
        return totalAnswers;
    }

    public void setTotalAnswers(Integer totalAnswers) {
        this.totalAnswers = totalAnswers;
    }

    public Integer getTotalUniqueQuestions() {
        return totalUniqueQuestions;
    }

    public void setTotalUniqueQuestions(Integer totalUniqueQuestions) {
        this.totalUniqueQuestions = totalUniqueQuestions;
    }

    public float getCorrectAnswers() {
        return correctAnswers;
    }

    public void setCorrectAnswers(float correctAnswers) {
        this.correctAnswers = correctAnswers;
    }

    public float getImprovedCorrectAnswers() {
        return improvedCorrectAnswers;
    }

    public void setImprovedCorrectAnswers(float improvedCorrectAnswers) {
        this.improvedCorrectAnswers = improvedCorrectAnswers;
    }

    public Integer getUniqueCorrectAnswers() {
        return uniqueCorrectAnswers;
    }

    public void setUniqueCorrectAnswers(Integer uniqueCorrectAnswers) {
        this.uniqueCorrectAnswers = uniqueCorrectAnswers;
    }

    public Integer getUniqueWrongAnswers() {
        return uniqueWrongAnswers;
    }

    public void setUniqueWrongAnswers(Integer uniqueWrongAnswers) {
        this.uniqueWrongAnswers = uniqueWrongAnswers;
    }

    public Integer getTotalAvailableQuestions() {
        return totalAvailableQuestions;
    }

    public void setTotalAvailableQuestions(Integer totalAvailableQuestions) {
        this.totalAvailableQuestions = totalAvailableQuestions;
    }

    public Integer getApprovedProposedSuggestions() {
        return approvedProposedSuggestions;
    }

    public Integer getTotalProposedSuggestions() {
        return totalProposedSuggestions;
    }

    public void setApprovedProposedSuggestions(Integer approvedProposedSuggestions) {
        this.approvedProposedSuggestions = approvedProposedSuggestions;
    }

    public void setTotalProposedSuggestions(Integer totalProposedSuggestions) {
        this.totalProposedSuggestions = totalProposedSuggestions;
    }

    public Integer getTotalQueriesSubmitted() { return totalQueriesSubmitted; }

    public void setTotalQueriesSubmitted(Integer totalQueriesSubmitted) { this.totalQueriesSubmitted = totalQueriesSubmitted; }

    public Integer getSharedQueries() { return sharedQueries; }

    public void setSharedQueries(Integer sharedQueries) { this.sharedQueries = sharedQueries; }

    @Override
    public String toString() {
        return "StatsDto{" +
                "totalQuizzes=" + totalQuizzes +
                ", totalAnswers=" + totalAnswers +
                ", totalUniqueQuestions=" + totalUniqueQuestions +
                ", correctAnswers=" + correctAnswers +
                ", improvedCorrectAnswers=" + improvedCorrectAnswers +
                ", uniqueCorrectAnswers=" + uniqueCorrectAnswers +
                ", uniqueWrongAnswers=" + uniqueWrongAnswers +
                ", totalAvailableQuestions=" + totalAvailableQuestions +
                ", totalProposedSuggestions=" + totalProposedSuggestions +
                ", approvedProposedSuggestions=" + approvedProposedSuggestions +
                ", totalQueriesSubmitted=" + totalQueriesSubmitted +
                ", sharedQueries=" + sharedQueries +
                ", privateSuggestionStats=" + privateSuggestionStats +
                '}';
    }
}
