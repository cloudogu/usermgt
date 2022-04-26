const {
    Then
} = require("cypress-cucumber-preprocessor/steps");

Then("the newly created user is asked to change his password", function () {
    cy.get('div[data-testid="login-reset-pw-msg"]').should('be.visible')
    cy.get('input[data-testid="password-input"]').should('be.visible')
    cy.get('input[data-testid="confirmedPassword-input"]').should('be.visible')
});

Then("the password reset flag is not visible", function () {
    cy.get('#pwdResetAtFirstLogin').should('not.be.visible')
})

Then("the password reset flag is unchecked", function () {
    cy.get('#pwdResetAtFirstLogin').should('be.visible')
    cy.get('#pwdResetAtFirstLogin').should('not.be.checked')
})

// TODO?
Then(/^the user has no administrator privileges in the dogu$/, function () {
    //not possible for cockpit as there is no distinction between normal user and admin
});

Then(/^the user has administrator privileges in the dogu$/, function () {
    //not possible for cockpit as there is no distinction between normal user and admin
});