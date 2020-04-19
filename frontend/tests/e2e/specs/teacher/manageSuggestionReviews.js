describe('Suggestion reviews walkthrough', () => {
  beforeEach(() => {
    cy.demoStudentLogin();
    cy.navigateSuggestions();
  });

  afterEach(() => {
    cy.contains('Logout').click();
  });

  it('login creates suggestion, login creates suggestion review, deletes them', () => {
    cy.createSuggestion('Test-Question', 'Test Question Content', [
      {
        correct: false,
        content: 'Option 1'
      },
      {
        correct: false,
        content: 'Option 2'
      },
      {
        correct: true,
        content: 'Option 3'
      },
      {
        correct: false,
        content: 'Option 4'
      }
    ]);

    cy.contains('Logout').click();

    cy.demoTeacherLogin();
    cy.navigateSuggestionsTeacher();
    cy.createSuggestionReview('Test-Question', false, 'Test-Justification');
    cy.navigateSuggestionReviews();
    cy.deleteSuggestionReview('Test-Question');
    cy.contains('Logout').click();

    cy.demoStudentLogin();
    cy.navigateSuggestions();
    cy.deleteSuggestion('Test-Question');
  });
});
