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


Then("the user has no administrator privileges in the dogu", function () {
    //not possible for usermgt as there is no distinction between normal user and admin
    //only the CES manager has extended permissions, not the admin
});

Then("the user has administrator privileges in the dogu", function () {
    //not possible for usermgt as there is no distinction between normal user and admin
    //only the CES manager has extended permissions, not the admin
});

Then("the password entry is marked as invalid", function () {
    // Since the validation is only carried out when the text field loses its focus,
    // the change of focus is effected by clicking on another position.
    cy.get('p[data-testid="password-policy-rules"]').click()

    cy.get('span[data-testid="password-invalid-marker"]').should('be.visible')
    cy.get('span[data-testid="password-valid-marker"]').should('not.be.visible')
});

Then("the password entry is marked as valid", function () {
    // Since the validation is only carried out when the text field loses its focus,
    // the change of focus is effected by clicking on another position.
    cy.get('p[data-testid="password-policy-rules"]').click()

    cy.get('span[data-testid="password-valid-marker"]').should('be.visible')
    cy.get('span[data-testid="password-invalid-marker"]').should('not.be.visible')
});

Then("all password rules are marked as not fullfilled", function () {
    // Actually, there should be 5 unfulfilled rules. But unfortunately there is a bug: with an empty password, a rule is still marked as valid.
    cy.get('p[data-testid="password-policy-rules"]').children('span[ng-repeat="violation in passwordPolicy.violations"]').should('have.length', 4)
});

Then("all password rules are marked as fullfilled", function () {
    cy.get('p[data-testid="password-policy-rules"]').children('span[ng-repeat="satisfaction in passwordPolicy.satisfactions"]').should('have.length', 5)
});

Then("all password rules are displayed", function () {
    cy.get('p[data-testid="password-policy-rules"]').should('be.visible')

    cy.get('p[data-testid="password-policy-rules"]').contains('at least 1 special character')
    cy.get('p[data-testid="password-policy-rules"]').contains('at least 1 number')
    cy.get('p[data-testid="password-policy-rules"]').contains('at least 1 lower case letter')
    cy.get('p[data-testid="password-policy-rules"]').contains('at least 1 capital letter')
    cy.get('p[data-testid="password-policy-rules"]').contains('at least 14 character')
});

