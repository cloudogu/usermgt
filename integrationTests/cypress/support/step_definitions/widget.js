const {
  Given,
  When,
  Then
} = require("cypress-cucumber-preprocessor/steps");

//
// WHEN
//
When(/^the user selects the latest dashboard from the navbar$/, function () {
  cy.get('a[id*="dashboard"]').last().click()
  cy.waitFor()
});

When(/^the user clicks on the edit\-dashboard\-icon$/, function () {
  cy.get('.glyphicon-edit').should('be.visible').click()
  cy.waitFor()
});

When(/^the user clicks on the glyphicon to add a new widget$/, function () {
  cy.get('.glyphicon-plus-sign').should('be.visible').click()
  cy.waitFor()
});

When(/^selects a github-widget$/, function () {
  cy.contains('GitHub').should('be.visible').click()
  cy.waitFor()
  cy.contains('GitHub Author').should('be.visible').click()
  cy.waitFor()
});

When(/^saves the new widget$/, function () {
  cy.get('.glyphicon-save').should('be.visible').click()
});

//
// THEN
//
Then(/^the new widget is displayed on the dashboard$/, function () {
  cy.get('div[class*="widget"]').should("exist")
});

Then(/^asks for a change in its widget\-configuration$/, function () {
  cy.contains('Please insert a repository path in the widget configuration').should('exist')
});
