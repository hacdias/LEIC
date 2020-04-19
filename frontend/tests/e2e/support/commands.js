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
Cypress.Commands.add('demoAdminLogin', () => {
  cy.visit('/');
  cy.get('[data-cy="adminButton"]').click();
  cy.contains('Administration').click();
  cy.contains('Manage Courses').click();
});

Cypress.Commands.add('createCourseExecution', (name, acronym, academicTerm) => {
  cy.get('[data-cy="createButton"]').click();
  cy.get('[data-cy="Name"]').type(name);
  cy.get('[data-cy="Acronym"]').type(acronym);
  cy.get('[data-cy="AcademicTerm"]').type(academicTerm);
  cy.get('[data-cy="saveButton"]').click();
});

Cypress.Commands.add('closeErrorMessage', (name, acronym, academicTerm) => {
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
    cy.get('[data-cy="Acronym"]').type(acronym);
    cy.get('[data-cy="AcademicTerm"]').type(academicTerm);
    cy.get('[data-cy="saveButton"]').click();
  }
);

Cypress.Commands.add('demoStudentLogin', () => {
  cy.visit('/');
  cy.get('[data-cy="studentButton"]').click();
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

Cypress.Commands.add('deleteSuggestion', title => {
  cy.contains(title)
    .parent()
    .parent()
    .should('have.length', 1)
    .children()
    .should('have.length', 6)
    .find('[data-cy="deleteSuggestionButton"]')
    .click();
});

Cypress.Commands.add('demoTeacherLogin', () => {
  cy.visit('/');
  cy.get('[data-cy="teacherButton"]').click();
});

Cypress.Commands.add('navigateSuggestionsTeacher', () => {
  cy.contains('Management').click();
  cy.contains('Suggestions').click();
});

Cypress.Commands.add('navigateSuggestionReviews', () => {
  cy.contains('Management').click();
  cy.contains('Suggestion Reviews').click();
});

Cypress.Commands.add('createSuggestionReview', (title, approved, justification) => {
  cy.contains(title)
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
});

Cypress.Commands.add('deleteSuggestionReview', title => {
  cy.contains(title)
    .parent()
    .parent()
    .should('have.length', 1)
    .children()
    .should('have.length', 4)
    .find('[data-cy="deleteSuggestionReviewButton"]')
    .click({ force: true });
});