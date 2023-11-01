describe('Questions walkthrough', () => {
  beforeEach(() => {
    cy.demoTeacherLogin();
    cy.navigateQuestions();
    cy.wait(3000);
  });

  afterEach(() => {
    cy.contains('Logout').click();
  });

  it('makes the first question available', () => {
    cy.get('[data-cy="statusSelect"]')
      .first()
      .parent()
      .parent()
      .click()
    
    cy.contains('AVAILABLE').first().click()
    cy.wait(500)
  });
});
