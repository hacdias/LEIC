// ***********************************************
// This example commands.js shows you how to
// create various custom commands and overwrite
// existing commands.
//
// For more comprehensive examples of custom
// commands please read more here:
// https://on.cypress.io/custom-commands
// ***********************************************
//
//
// -- This is a parent command --
// Cypress.Commands.add("login", (email, password) => { ... })
//
//
// -- This is a child command --
// Cypress.Commands.add("drag", { prevSubject: 'element'}, (subject, options) => { ... })
//
//
// -- This is a dual command --
// Cypress.Commands.add("dismiss", { prevSubject: 'optional'}, (subject, options) => { ... })
//
//
// -- This will overwrite an existing command --
// Cypress.Commands.overwrite("visit", (originalFn, url, options) => { ... })
/// <reference types="Cypress" />

Cypress.Commands.add('createCourseExecution', (name, acronym, academicTerm) => {
  cy.get('[data-cy="createButton"]').click();
  cy.get('[data-cy="courseExecutionNameInput"]').type(name);
  cy.get('[data-cy="courseExecutionAcronymInput"]').type(acronym);
  cy.get('[data-cy="courseExecutionAcademicTermInput"]').type(academicTerm);
  cy.get('[data-cy="saveButton"]').click();
});

Cypress.Commands.add('closeErrorMessage', () => {
  cy.contains('Error')
    .parent()
    .find('button')
    .click();
});

Cypress.Commands.add('deleteCourseExecution', acronym => {
  cy.contains(acronym)
    .parent()
    .should('have.length', 1)
    .children()
    .should('have.length', 7)
    .find('[data-cy="deleteCourse"]')
    .click();
});

Cypress.Commands.add(
  'createFromCourseExecution',
  (name, acronym, academicTerm) => {
    cy.contains(name)
      .parent()
      .should('have.length', 1)
      .children()
      .should('have.length', 7)
      .find('[data-cy="createFromCourse"]')
      .click();
    cy.get('[data-cy="courseExecutionAcronymInput"]').type(acronym);
    cy.get('[data-cy="courseExecutionAcademicTermInput"]').type(academicTerm);
    cy.get('[data-cy="saveButton"]').click();
  }
);

Cypress.Commands.add('demoStudentLogin', () => {
  cy.visit('/');
  cy.get('[data-cy="demoStudentLoginButton"]').click();
});

Cypress.Commands.add('demoTeacherLogin', () => {
  cy.visit('/');
  cy.get('[data-cy="demoTeacherLoginButton"]').click();
});

Cypress.Commands.add('navigateSuggestions', () => {
  cy.get('[data-cy="suggestionsButton"]').click();
});

Cypress.Commands.add('createSuggestion', (name, content, options) => {
  cy.get('[data-cy="createSuggestionButton"]').click();
  cy.get('[data-cy="Title"]').focus();
  cy.get('[data-cy="Title"]').type(name);
  cy.get('[data-cy="Content"]').focus();
  cy.get('[data-cy="Content"]').type(content);

  for (let i = 0; i < options.length; i++) {
    cy.get(`[data-cy="OptionCorrect[${i}]"]`).focus();
    if (options[i].correct) {
      // Checkbox is hidden so we need to force.
      cy.get(`[data-cy="OptionCorrect[${i}]"]`).check({ force: true });
    }
    cy.get(`[data-cy="OptionContent[${i}]"]`).focus();
    cy.get(`[data-cy="OptionContent[${i}]"]`).type(options[i].content);
  }

  cy.get('[data-cy="saveSuggestionButton"]').click();
});

Cypress.Commands.add('answerQuiz', quizNumber => {
  cy.get('.list-header > :nth-child(1)').click(); //Avoiding problems with topbar
  cy.get(`ul > :nth-child(${quizNumber + 2})`).click();

  cy.get('.end-quiz').click();
  cy.get('.primary--text > .v-btn__content').click();
});

Cypress.Commands.add('deleteSuggestion', title => {
  cy.contains(title)
    .parent()
    .parent()
    .parent()
    .should('have.length', 1)
    .children()
    .should('have.length', 6)
    .find('[data-cy="deleteSuggestionButton"]')
    .click();
});

Cypress.Commands.add('navigateAvailableQuizzes', () => {
  cy.get('[data-cy="quizzesButton"]').click();
  cy.get('[data-cy="quizzesAvailableButton"]').click();
});

Cypress.Commands.add('navigateQueriesStudents', () => {
  cy.get('[data-cy="queriesButton"]').click();
  cy.get('[data-cy="queriesSubmittedButton"]').click();
});

Cypress.Commands.add('navigateQueriesTeachers', () => {
  cy.get('[data-cy="managementButton"]').click();
  cy.get('[data-cy="queriesSubmittedButton"]').click();
});

Cypress.Commands.add('navigateQuery', title => {
  cy.get('.list-header > :nth-child(1)').click(); //Avoiding problems with topbar
  cy.contains(title)
    .parent()
    .click();
});

Cypress.Commands.add('verifyQuery', (title, content) => {
  cy.get('[data-cy=queryComponent]')
    .find('[data-cy="queryTitle"]')
    .should('have.text', ' ' + title + ' ');
  cy.get('[data-cy=queryComponent]')
    .find('[data-cy="queryContent"]')
    .should('have.text', ' ' + content + ' ');
});

Cypress.Commands.add('createQuery', (name, content) => {
  cy.get('[data-cy="createQueryButton"]').click();
  cy.get('[data-cy="Title"]').focus();
  cy.get('[data-cy="Title"]').type(name);
  cy.get('[data-cy="Content"]').focus();
  cy.get('[data-cy="Content"]').type(content);

  cy.get('[data-cy="saveQueryButton"]').click();
});

Cypress.Commands.add('editQuery', (name, content) => {
  cy.get('[data-cy=queryComponent]')
    .find('[data-cy="editQueryButton"]')
    .click();

  cy.get('[data-cy="Title"]').focus();
  cy.get('[data-cy="Title"]').clear();
  if (name != '') cy.get('[data-cy="Title"]').type(name);

  cy.get('[data-cy="Content"]').focus();
  cy.get('[data-cy="Content"]').clear();
  if (content != '') cy.get('[data-cy="Content"]').type(content);

  cy.get('[data-cy="saveQueryButton"]').click();
});

Cypress.Commands.add('appendQuery', (name, content) => {
  cy.get('[data-cy=queryComponent]')
    .find('[data-cy="editQueryButton"]')
    .click();

  cy.get('[data-cy="Title"]').focus();
  cy.get('[data-cy="Title"]').type(name);
  cy.get('[data-cy="Content"]').focus();
  cy.get('[data-cy="Content"]').type(content);

  cy.get('[data-cy="saveQueryButton"]').click();
});

Cypress.Commands.add('deleteQuery', () => {
  cy.get('[data-cy=queryComponent]')
    .find('[data-cy="deleteQueryButton"]')
    .click();
});

Cypress.Commands.add('verifyQueryAnswer', content => {
  cy.get('[data-cy=queryAnswerComponent]')
    .first()
    .find('[data-cy="queryAnswerContent"]')
    .should('have.text', ' ' + content + ' ');
});

Cypress.Commands.add('createQueryAnswer', content => {
  cy.get('[data-cy="createQueryAnswerButton"]').click();
  cy.get('[data-cy="Content"]').focus();
  cy.get('[data-cy="Content"]').type(content);

  cy.get('[data-cy="saveQueryAnswerButton"]').click();
});

Cypress.Commands.add('editQueryAnswer', content => {
  cy.get('[data-cy=queryAnswerComponent]')
    .first()
    .find('[data-cy="editQueryAnswerButton"]')
    .click();

  cy.get('[data-cy="Content"]').focus();
  cy.get('[data-cy="Content"]').clear();
  if (content != '') cy.get('[data-cy="Content"]').type(content);

  cy.get('[data-cy="saveQueryAnswerButton"]').click();
});

Cypress.Commands.add('appendQueryAnswer', content => {
  cy.get('[data-cy=queryAnswerComponent]')
    .first()
    .find('[data-cy="editQueryAnswerButton"]')
    .click();

  cy.get('[data-cy="Content"]').focus();
  cy.get('[data-cy="Content"]').type(content);

  cy.get('[data-cy="saveQueryAnswerButton"]').click();
});

Cypress.Commands.add('deleteQueryAnswer', () => {
  cy.get('[data-cy=queryAnswerComponent]')
    .first()
    .find('[data-cy="deleteQueryAnswerButton"]')
    .click();
});

Cypress.Commands.add('navigateTournaments', () => {
  cy.get('[data-cy="tournamentsButton"]').click();
});

Cypress.Commands.add('createTournament', (name, numberQuestions) => {
  cy.get('[data-cy="newTournamentButton"]').click();

  cy.get('[data-cy="Title"]').focus();
  cy.get('[data-cy="Title"]').type(name);

  cy.get('[data-cy="numberQuestions"]').focus();
  cy.get('[data-cy="numberQuestions"]').type(numberQuestions);

  cy.get('[data-cy="availableDate"]').click();
  cy.get('.datepicker-controls')
    .first()
    .find('.datepicker-button')
    .first()
    .click({ force: true });
  cy.get('.slideprev-enter-active')
    .find(':nth-child(25)')
    .click({ force: true });
  cy.get('.datepicker-buttons-container')
    .find('.validate')
    .first()
    .click({ force: true });

  cy.get('[data-cy="conclusionDate"]').click();
  cy.get('#conclusionDateInput-picker-container-DatePicker')
    .find('.calendar')
    .find('.datepicker-controls')
    .find('.text-right')
    .find('.datepicker-button')
    .find('svg')
    .click({ force: true });
  cy.get('.slidenext-enter-active')
    .find(':nth-child(26)')
    .click({ force: true });
  cy.get('#conclusionDateInput-wrapper')
    .find('.datetimepicker')
    .find('.datepicker')
    .find('.datepicker-buttons-container')
    .find('.validate')
    .click({ force: true });

  cy.contains('add').click();

  cy.get('[data-cy="saveTournamentButton"]').click();
});

Cypress.Commands.add('enrollTournament', name => {
  cy.contains('add').click();
  cy.contains('Yes')
    .parent()
    .should('have.text', 'Yes');
});

Cypress.Commands.add('navigateSuggestionsTeacher', () => {
  cy.contains('Management').click();
  cy.contains('Suggestions').click();
});

Cypress.Commands.add('navigateSuggestionReviews', () => {
  cy.contains('Management').click();
  cy.contains('Suggestion Reviews').click();
});

Cypress.Commands.add(
  'createSuggestionReview',
  (title, approved, justification) => {
    cy.contains(title)
      .parent()
      .parent()
      .parent()
      .should('have.length', 1)
      .children()
      .should('have.length', 6)
      .find('[data-cy="createSuggestionReviewButton"]')
      .click();

    cy.get('[data-cy="Approved"]').focus();
    if (approved) {
      cy.get('[data-cy="Approved"]').check();
    }
    cy.get('[data-cy="Justification"]').focus();
    cy.get('[data-cy="Justification"]').type(justification);

    cy.get('[data-cy="saveSuggestionReviewButton"]').click();
  }
);

Cypress.Commands.add('deleteSuggestionReview', title => {
  cy.contains(title)
    .parent()
    .parent()
    .parent()
    .should('have.length', 1)
    .children()
    .should('have.length', 4)
    .find('[data-cy="deleteSuggestionReviewButton"]')
    .click({ force: true });
});
