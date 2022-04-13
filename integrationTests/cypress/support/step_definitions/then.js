const {
    Then
} = require("cypress-cucumber-preprocessor/steps");

Then("the newly created user is asked to change his password", function () {
    cy.get('div[data-testid="login-reset-pw-msg"]').should('be.visible')
    cy.get('input[data-testid="password-input"]').should('be.visible')
    cy.get('input[data-testid="confirmedPassword-input"]').should('be.visible')
});

Then("the password reset flag is disabled", function () {
    cy.get('#pwdResetAtFirstLogin').should('be.visible')
    cy.get('#pwdResetAtFirstLogin').should('be.disabled')
})