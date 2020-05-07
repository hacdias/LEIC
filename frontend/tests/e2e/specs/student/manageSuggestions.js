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

  it('login creates suggestion, login creates suggestion review, login edits suggestion and deletes it', () => {
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

    cy.contains('Logout').click();

    cy.demoStudentLogin();
    cy.navigateSuggestions();

    cy.editSuggestion('Test-Question', [
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
    ], 'Edited Title', 'Edited Question Content', [
      {
        correct: false,
        content: 'Edited Option 1'
      },
      {
        correct: true,
        content: 'Edited Option 2'
      },
      {
        correct: false,
        content: 'Edited Option 3'
      },
      {
        correct: false,
        content: 'Edited Option 4'
      }
    ]);

    cy.contains('Logout').click();

    cy.demoTeacherLogin();
    cy.navigateQuestions();
    cy.deleteQuestion('Edited Title');
  });
});
