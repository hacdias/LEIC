const loadTournament = 'tests/e2e/support/tournament/loadTournament.sql'

describe('Tournament walkthrough', () => {


  beforeEach(() => {
    cy.demoStudentLogin();
    cy.navigateTournaments();
  });

  afterEach(() => {
    cy.contains('Logout').click();
  });

  it('login sees and creates a tournament', () => {
    cy.createTournament('Test-Tournament', '3');
  });

  it('login sees and enrolls a tournament', () => {
    cy.enrollTournament();
  });

  it('login sees and cancels tournament', () => {
    cy.cancelTournament();
  });

  it('login sees and enrolls a tournament', () => {
    cy.enrollTournament();
  });
  it('login sees, enrolls and checks quiz generated', () => {
    cy.checkQuizGeneration();
  });
});
