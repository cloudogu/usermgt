const {
    Then
} = require("cypress-cucumber-preprocessor/steps");

    Then("the newly created user is asked to change his password", function () {
        cy.get('div[data-testid="login-reset-pw-msg"]').should('be.visible')
        cy.get('input[data-testid="password-input"]').should('be.visible')
        cy.get('input[data-testid="confirmedPassword-input"]').should('be.visible')
    });