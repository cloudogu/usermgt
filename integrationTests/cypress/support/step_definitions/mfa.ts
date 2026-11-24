import {Before, Given, Then} from "@badeball/cypress-cucumber-preprocessor";

Before({tags: "@mfa_active"}, () => {
    cy.intercept("GET", "/usermgt/api/account/gui_config", {
        pwdResetPreselected: false,
        externalLdap: false,
        mfaActive: true,
    });
});

Before({tags: "@mfa_inactive"}, () => {
    cy.intercept("GET", "/usermgt/api/account/gui_config").as("getGuiConfig");
});

Then("the GUI configuration reports that MFA is disabled", function () {
    cy.wait("@getGuiConfig").its("response.body.mfaActive").should("eq", false);
});

Given("the MFA request for user {string} is observed", function (username: string) {
    cy.intercept("GET", `/usermgt/api/mfa/${username}`).as("getMfa");
});

Then("the MFA request returns status {int}", function (status: number) {
    cy.wait("@getMfa").its("response.statusCode").should("eq", status);
});

Then("MFA management is not shown in the user form", function () {
    cy.contains("h2", "Two-Factor Authentication").should("not.exist");
    cy.get('[data-testid="mfa-delete-dialog"]').should("not.exist");
});

Then("the MFA notice for new users is shown in the user form", function () {
    cy.contains("h2", "Two-Factor Authentication").should("be.visible");
    cy.contains("p", "Two-Factor Authentication for new users is active and not configured. The new user must configure the second factor on the next login.")
        .should("be.visible");
});
