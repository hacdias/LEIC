describe('Queries and Answers walkthrough', () => {
  before(() => {
    cy.demoStudentLogin();
    cy.navigateAvailableQuizzes();
    cy.answerQuiz(0);
  });

  it('login student creates and updates a query', () => {
    cy.createQuery('Test Query', 'Test Query Content');

    cy.navigateQueriesStudents();
    cy.navigateQuery('Test Query');
    cy.verifyQuery('Test Query', 'Test Query Content');

    cy.editQuery('Test Query Updated', 'Test Query Content Updated');
    cy.verifyQuery('Test Query Updated', 'Test Query Content Updated');
    cy.appendQuery(' Again', ' Again');
    cy.verifyQuery(
      'Test Query Updated Again',
      'Test Query Content Updated Again'
    );

    cy.contains('Logout').click();
  });

  it('login student invalid update a query', () => {
    cy.demoStudentLogin();
    cy.navigateQueriesStudents();
    cy.navigateQuery('Test Query Updated Again');

    cy.editQuery('', 'Invalid Query');
    cy.closeErrorMessage();
    cy.get('[data-cy="cancelButton"]').click();
    cy.verifyQuery(
      'Test Query Updated Again',
      'Test Query Content Updated Again'
    );

    cy.editQuery('Invalid Query', '');
    cy.closeErrorMessage();
    cy.get('[data-cy="cancelButton"]').click();
    cy.verifyQuery(
      'Test Query Updated Again',
      'Test Query Content Updated Again'
    );

    cy.editQuery('', '');
    cy.closeErrorMessage();
    cy.get('[data-cy="cancelButton"]').click();
    cy.verifyQuery(
      'Test Query Updated Again',
      'Test Query Content Updated Again'
    );

    cy.contains('Logout').click();
  });

  it('login teacher creates and updates a query answer', () => {
    cy.demoTeacherLogin();
    cy.navigateQueriesTeachers();
    cy.navigateQuery('Test Query Updated Again');
    cy.createQueryAnswer('Test Query Answer Content');

    cy.editQueryAnswer('Test Query Answer Content Updated');
    cy.verifyQueryAnswer('Test Query Answer Content Updated');
    cy.appendQueryAnswer(' Again');
    cy.verifyQueryAnswer('Test Query Answer Content Updated Again');

    cy.contains('Logout').click();
  });

  it('login teacher invalid update a query answer', () => {
    cy.demoTeacherLogin();
    cy.navigateQueriesTeachers();
    cy.navigateQuery('Test Query Updated Again');

    cy.editQueryAnswer('');
    cy.closeErrorMessage();
    cy.get('[data-cy="cancelButton"]').click();
    cy.verifyQueryAnswer('Test Query Answer Content Updated Again');

    cy.contains('Logout').click();
  });

  it('login teacher share a query', () => {
    cy.demoTeacherLogin();
    cy.navigateQueriesTeachers();
    cy.navigateQuery('Test Query Updated Again');

    cy.shareQuery();

    cy.contains('Logout').click();
  });

  it('login student check shared queries', () => {
    cy.demoStudentLogin();
    cy.navigateSolvedQuizzes();
    cy.checkSharedQueries();

    cy.navigateQuery('Test Query Updated Again');
    cy.verifyQuery(
      'Test Query Updated Again',
      'Test Query Content Updated Again'
    );
    cy.verifyQueryAnswer('Test Query Answer Content Updated Again');

    cy.contains('Logout').click();
  });

  it('login student visualizes an answer of teacher', () => {
    cy.demoStudentLogin();
    cy.navigateQueriesStudents();
    cy.navigateQuery('Test Query Updated Again');
    cy.verifyQueryAnswer('Test Query Answer Content Updated Again');

    cy.contains('Logout').click();
  });

  it('login student tries to delete a query', () => {
    cy.demoStudentLogin();
    cy.navigateQueriesStudents();
    cy.navigateQuery('Test Query Updated Again');
    cy.deleteQuery();
    cy.closeErrorMessage();
    cy.contains('Logout').click();
  });

  it('login teacher deletes a query answer', () => {
    cy.demoTeacherLogin();
    cy.navigateQueriesTeachers();
    cy.navigateQuery('Test Query Updated Again');
    cy.deleteQueryAnswer();
    cy.contains('Logout').click();
  });

  it('login student deletes a query', () => {
    cy.demoStudentLogin();
    cy.navigateQueriesStudents();
    cy.navigateQuery('Test Query Updated Again');
    cy.deleteQuery();
    cy.contains('Logout').click();
  });
});
