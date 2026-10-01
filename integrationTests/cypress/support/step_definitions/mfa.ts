import {Given, Then} from "@badeball/cypress-cucumber-preprocessor";

Given("the MFA request for user {string} is observed", function (username: string) {
    cy.intercept("GET", `/usermgt/api/mfa/${username}`).as("getMfa");
});

Then("MFA management is not shown in the user form", function () {
    // Wait for the real response: disabled MFA is reported as unavailable.
    cy.wait("@getMfa").its("response.statusCode").should("eq", 503);
    cy.contains("h2", "Two-Factor Authentication").should("not.exist");
    cy.get('[data-testid="mfa-delete-dialog"]').should("not.exist");
});
