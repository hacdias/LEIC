describe('Suggestions walkthrough', () => {
  beforeEach(() => {
    cy.demoStudentLogin();
    cy.navigateSuggestions();
  });

  afterEach(() => {
    cy.contains('Logout').click();
  });

  it('login creates and deletes a suggestion', () => {
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

    cy.deleteSuggestion('Test-Question');
  });

  it('login creates two suggestions and deletes them', () => {
    cy.createSuggestion('Test-Question1', 'Test Question Content', [
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

    cy.createSuggestion('Test-Question2', 'Test Question Content', [
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

    cy.deleteSuggestion('Test-Question1');
    cy.deleteSuggestion('Test-Question2');
  });
});
