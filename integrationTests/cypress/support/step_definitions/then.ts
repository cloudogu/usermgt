import { Then } from "@badeball/cypress-cucumber-preprocessor";
import env from "@cloudogu/dogu-integration-test-library/environment_variables";




Then("the newly created user is asked to change his password", function () {
    cy.get('div[data-testid="login-reset-pw-msg"]').should('be.visible')
    cy.get('input[data-testid="password-input"]').should('be.visible')
    cy.get('input[data-testid="confirmedPassword-input"]').should('be.visible')
});

Then("the password reset flag is not visible", function () {
    cy.get('label[data-testid="pwdReset-label"]').should('not.exist')
})

Then("the password reset flag is unchecked", function () {
    cy.get('label[data-testid="pwdReset-label"]').should('be.visible')
    cy.get('input[data-testid="pwdReset-checkbox"]').should('not.be.checked')
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
    cy.get('input[data-testid="confirmPassword-input"]').click()
    cy.get('input[data-testid="password-input"]').should('have.class', 'border-textfield-danger-border')
    cy.get('div[data-testid="password-input-error-errors"]').should('be.visible')
});

Then("the password entry is marked as valid", function () {
    // Since the validation is only carried out when the text field loses its focus,
    // the change of focus is effected by clicking on another position.
    cy.get('input[data-testid="confirmPassword-input"]').click()
    cy.get('input[data-testid="password-input"]').should('have.class', 'border-textfield-success-border')
    cy.get('div[data-testid="password-input-error-errors"]').should('not.exist')
});

Then("all password rules are displayed", function () {
    cy.get('span[data-testid="password-input-error-0"]').should('be.visible')
    cy.get('span[data-testid="password-input-error-1"]').should('be.visible')
    cy.get('span[data-testid="password-input-error-2"]').should('be.visible')
    cy.get('span[data-testid="password-input-error-3"]').should('be.visible')
    cy.get('span[data-testid="password-input-error-4"]').should('be.visible')
    cy.get('span[data-testid="password-input-error-5"]').should('be.visible')

    cy.get('div[data-testid="password-input-error-errors"]').contains('The password must contain at least 14 characters.')
    cy.get('div[data-testid="password-input-error-errors"]').contains('The password must contain at least one capital letter.')
    cy.get('div[data-testid="password-input-error-errors"]').contains('The password must contain at least one lower case letter.')
    cy.get('div[data-testid="password-input-error-errors"]').contains('The password must contain at least 1 number.')
    cy.get('div[data-testid="password-input-error-errors"]').contains('The password must contain at least 1 special character.')
    cy.get('div[data-testid="password-input-error-errors"]').contains('The password must not contain only spaces.')
});

Then("the password-confirm entry is marked as invalid", function () {
    // Since the validation is only carried out when the text field loses its focus,
    // the change of focus is effected by clicking on another position.
    cy.get('input[data-testid="password-input"]').click()
    cy.get('input[data-testid="confirmPassword-input"]').should('have.class', 'border-textfield-danger-border')
    cy.get('div[data-testid="confirmPassword-input-error-errors"]').should('be.visible')
});

Then("the password-confirm entry is marked as valid", function () {
    // Since the validation is only carried out when the text field loses its focus,
    // the change of focus is effected by clicking on another position.
    cy.get('input[data-testid="password-input"]').click()
    cy.get('input[data-testid="confirmPassword-input"]').should('have.class', 'border-textfield-success-border')
    cy.get('div[data-testid="confirmPassword-input-error-errors"]').should('not.exist')
});

Then("the password-confirm rules are displayed", function () {
    cy.get('span[data-testid="confirmPassword-input-error-0"]').should('be.visible')
    cy.get('div[data-testid="confirmPassword-input-error-errors"]').contains('Passwords must match.')
});

Then("the import finished with status code {int}", (statusCode) => {
    cy.get("@responseStatus").then((status) => {
       expect(status).to.eq(statusCode)
    });
})

Then("the access to the endpoint is denied", function () {
    then((response) => {
        expect(response.status).to.eq(400)
    })
})

Then("the user {string} was created",function (username) {
    cy.api({
        method: "GET",
        url: Cypress.config().baseUrl + "/usermgt/api/users/" + username,
        auth: {
            'user': env.GetAdminUsername(),
            'pass': env.GetAdminPassword
        },
    }).then((response) => {
        expect(response.status).to.eq(200)
    })
})

Then("the user {string} does not exists",function (username) {
    cy.clearCookies();
    cy.request({
        method: "GET",
        url: Cypress.config().baseUrl + "/usermgt/api/users/" + username,
        auth: {
            'user': env.GetAdminUsername(),
            'pass': env.GetAdminPassword()
        },
        failOnStatusCode: false,
    }).then((response) => {
        expect(response.status).to.eq(404)
    })
})


