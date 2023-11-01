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

  it('see queries statistics', () => {
    cy.get('[data-cy="totalQueriesSubmitted"]')
      .invoke('text')
      .then(text => {
        expect(text.trim()).to.match(/^[0-9]+ Total$/);
      });

    cy.get('[data-cy="sharedQueries"]')
      .invoke('text')
      .then(text => {
        expect(text.trim()).to.match(/^[0-9]+ Shared$/);
      });
  });

  it('see tournament statistics', () => {
    cy.get('[data-cy="enrolledTournaments"]')
      .invoke('text')
      .then(text => {
        expect(text.trim()).to.match(/^[0-9]+ Enrolled$/);
      });

    cy.get('[data-cy="totalTournamentAnswers"]')
      .invoke('text')
      .then(text => {
        expect(text.trim()).to.match(/^[0-9]+ Tournament Quiz Answers$/);
      });
  });

  it('toggle suggestions stats privacy', () => {
    cy.get('[data-cy="suggestionPrivacyToggler"]').check({ force: true }).should('be.checked')      
  });

  it('toggle queries stats privacy', () => {
    cy.get('[data-cy="queryPrivacyToggler"]').check({ force: true }).should('be.checked')
  });

  it('toggle tournaments stats privacy', () => {
    cy.get('[data-cy="tournamentPrivacyToggler"]').check({ force: true }).should('be.checked')
  });

});
