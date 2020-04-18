describe('Queries walkthrough', () => {
    beforeEach(() => {
      cy.demoStudentLogin();
      cy.navigateAvailableQuizzes();
      cy.answerQuiz(0);
    });
  
    afterEach(() => {
      cy.contains('Logout').click();
    });
  
    it('login creates, updates and deletes a query', () => {
      cy.createQuery('Test Query', 'Test Query Content');
  
      cy.navigateQueriesStudents();
      cy.navigateQuery('Test Query');
      cy.verifyQuery('Test Query', 'Test Query Content');
      
      cy.editQuery('Test Query Updated', 'Test Query Content Updated');
      cy.verifyQuery('Test Query Updated', 'Test Query Content Updated');
      cy.appendQuery(' Again', ' Again');
      cy.verifyQuery('Test Query Updated Again', 'Test Query Content Updated Again');

      cy.deleteQuery();
    });
  });
  