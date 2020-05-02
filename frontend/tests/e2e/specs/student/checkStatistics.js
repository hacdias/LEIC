describe('Check Statistics', () => {
  beforeEach(() => {
    cy.demoStudentLogin();
    cy.navigateToStats();
  });

  afterEach(() => {
    cy.contains('Logout').click();
  });

  it('see suggestions statistics', () => {
    cy.get('[data-cy="totalSuggestions"]')
      .invoke('text')
      .then(text => {
        expect(text.trim()).to.match(/^[0-9]+ Total$/);
      });

    cy.get('[data-cy="approvedSuggestions"]')
      .invoke('text')
      .then(text => {
        expect(text.trim()).to.match(/^[0-9]+ Approved$/);
      });
  });
});
