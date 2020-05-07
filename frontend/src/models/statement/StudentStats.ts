export default class StudentStats {
  totalQuizzes!: number;
  totalAnswers!: number;
  totalUniqueQuestions!: number;
  correctAnswers!: number;
  improvedCorrectAnswers!: number;
  totalAvailableQuestions!: number;
  uniqueCorrectAnswers!: number;
  uniqueWrongAnswers!: number;
  totalProposedSuggestions!: number;
  approvedProposedSuggestions!: number;
  totalQueriesSubmitted!: number;
  sharedQueries!: number;
  enrolledTournaments!: number;
  correctTournamentAnswers!: number;
  totalTournamentAnswers!: number;

  privateSuggestionStats!: Boolean;
  privateQueryStats!: Boolean;
  privateTournamentStats!: Boolean;

  constructor(jsonObj?: StudentStats) {
    if (jsonObj) {
      this.totalQuizzes = jsonObj.totalQuizzes;
      this.totalAnswers = jsonObj.totalAnswers;
      this.totalUniqueQuestions = jsonObj.totalUniqueQuestions;
      this.correctAnswers = jsonObj.correctAnswers;
      this.improvedCorrectAnswers = jsonObj.improvedCorrectAnswers;
      this.uniqueCorrectAnswers = jsonObj.uniqueCorrectAnswers;
      this.uniqueWrongAnswers = jsonObj.uniqueWrongAnswers;
      this.totalAvailableQuestions = jsonObj.totalAvailableQuestions;
      this.totalProposedSuggestions = jsonObj.totalProposedSuggestions;
      this.approvedProposedSuggestions = jsonObj.approvedProposedSuggestions;
      this.totalQueriesSubmitted = jsonObj.totalQueriesSubmitted;
      this.sharedQueries = jsonObj.sharedQueries;
      this.enrolledTournaments = jsonObj.enrolledTournaments;
      this.correctTournamentAnswers = jsonObj.correctTournamentAnswers;
      this.totalTournamentAnswers = jsonObj.totalTournamentAnswers;
      this.privateSuggestionStats = jsonObj.privateSuggestionStats;
      this.privateQueryStats = jsonObj.privateQueryStats;
      this.privateTournamentStats = jsonObj.privateTournamentStats;
    }
  }
}
