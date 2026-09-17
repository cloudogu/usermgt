import {Then, When} from "@badeball/cypress-cucumber-preprocessor";
import {patElement, selectPATScope} from "./pat-helpers";

When("the user opens the PAT creation form", () => {
    cy.intercept("POST", "**/usermgt/api/pats").as("validationPAT");
    patElement("security-create-pat").click();
    patElement("security-create-pat-name-input").should("be.visible");
});

When("the user enters the PAT validation name {string}", (kind: string) => {
    const unique = `validation-${Date.now()}-${Cypress._.random(100000, 999999)}`;
    const names: Record<string, string> = {
        empty: "",
        "65 characters": unique.padEnd(65, "a"),
        "64 characters": unique.padEnd(64, "a"),
        "embedded space": "invalid name",
        "leading space": " invalid",
        "trailing space": "invalid ",
        "only spaces": "   ",
        "non-breaking space": "invalid\u00a0name",
        "zero-width space": "invalid\u200bname",
        "visible special characters": `${unique}-!@#$%&+_ä`,
        valid: unique,
    };
    expect(names).to.have.property(kind);
    const name = names[kind];
    cy.wrap(name).as("validationPATName");
    patElement("security-create-pat-name-input").clear();
    if (name) patElement("security-create-pat-name-input").type(name, {parseSpecialCharSequences: false});
});

When("the user enters the existing PAT name {string}", (alias: string) => {
    cy.get<string>(`@patName-${alias}`).then(name => {
        patElement("security-create-pat-name-input").type(name);
    });
});

When("the user selects the PAT expiry option {string}", (days: string) => {
    patElement("security-create-pat-expiry-select-trigger").click();
    patElement(`security-create-pat-expiry-${days}`).click();
});

When("the user selects the PAT scope {string}", selectPATScope);

When("the user submits the PAT validation form", () => {
    patElement("security-create-pat-submit").click();
});

Then("the PAT validation message {string} is visible", (message: string) => {
    cy.contains(message).should("be.visible");
});

Then("the PAT name error is {string}", (message: string) => {
    patElement("security-create-pat-name-input").should("have.attr", "aria-invalid", "true");
    cy.contains(message).should("be.visible");
});

Then("no PAT creation request was sent", () => {
    // Submission validates synchronously; the form must remain open.
    patElement("security-create-pat-submit").should("be.visible");
    cy.get("@validationPAT.all").should("have.length", 0);
});

function assertValidationPATCreated(withoutExpiration = false): void {
    cy.wait("@validationPAT").then(({request, response}) => {
        expect(response?.statusCode).to.be.within(200, 299);
        cy.get<string[]>("@createdPATIds").then(ids => {
            ids.push(response!.body.id);
        });
        cy.get<string>("@validationPATName").then(name => {
            expect(request.body.displayName).to.eq(name);
            expect(response!.body.displayName).to.eq(name);
        });
        expect(request.body.scope).to.eq("/usermgt");
        if (withoutExpiration) {
            expect(request.body.expiresAt).to.be.undefined;
            expect(response!.body.expiresAt == null, "PAT has no expiration").to.eq(true);
        }
    });
    patElement("security-created-pat-close").should("be.visible").click();
}

Then("the validation PAT is created", () => assertValidationPATCreated());
Then("the validation PAT is created without expiration", () => assertValidationPATCreated(true));

Then("the PAT dogu selection is invalid", () => {
    cy.get('[role="radiogroup"]').should("have.attr", "aria-invalid", "true");
});
