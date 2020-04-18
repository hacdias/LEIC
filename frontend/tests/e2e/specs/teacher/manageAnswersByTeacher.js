describe('Query Answers walkthrough', () => {
    beforeEach(() => {
      cy.demoStudentLogin();
      cy.navigateAvailableQuizzes();
      cy.answerQuiz(0);
      cy.createQuery('Test Query', 'Test Query Content');

      cy.contains('Logout').click();
      cy.demoTeacherLogin();
    });
  
    afterEach(() => {
      cy.contains('Logout').click();

      cy.demoStudentLogin();
      cy.navigateQueriesStudents();
      cy.navigateQuery('Test Query');
      cy.deleteQuery();
      cy.contains('Logout').click();
    });
  
    it('login creates, updates and deletes a query answer', () => {
      cy.navigateQueriesTeachers();
      cy.navigateQuery('Test Query');
      cy.createQueryAnswer('Test Query Answer Content');

      cy.editQueryAnswer('Test Query Answer Content Updated');
      cy.verifyQueryAnswer('Test Query Answer Content Updated');
      cy.appendQueryAnswer(' Again');
      cy.verifyQueryAnswer('Test Query Answer Content Updated Again');

      cy.deleteQueryAnswer();
    });
  });
  