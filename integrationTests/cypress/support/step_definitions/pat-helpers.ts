// Keep DOM selectors in one place for the security-page steps.
export const patElement = (testId: string) => cy.get(`[data-testid="${testId}"]`);

export const patCountBadge = () => patElement("security-pat-count").should("be.visible");

export function firstPATPage(): void {
    patElement("personal-access-tokens-pagination-back").then(button => {
        // Theme buttons express their disabled state through aria-disabled.
        if (!button.is(':disabled, [aria-disabled="true"]')) {
            cy.wrap(button).click();
            firstPATPage();
        }
    });
}

export function findPATRow(name: string): void {
    patElement("personal-access-tokens-table").should("be.visible").then(table => {
        const row = table.find("tbody tr").filter((_, element) =>
            Cypress.$(element).find("a").text().trim() === name);
        if (row.length) {
            cy.wrap(row).should("have.length", 1).and("be.visible").as("patRow");
            return;
        }
        patElement("personal-access-tokens-pagination-forward")
            .should("not.be.disabled")
            .and("not.have.attr", "aria-disabled", "true").click();
        findPATRow(name);
    });
}

export function selectPATScope(scope: string): void {
    if (scope === "Alle Dogus") {
        patElement("security-pat-all-dogus").click().should("have.attr", "aria-checked", "true");
    } else {
        expect(scope, "single dogu scope").to.match(/^\/[^/]+$/);
        patElement("security-pat-selected-dogus").click().should("have.attr", "aria-checked", "true");
        patElement(`security-pat-dogu-${scope.slice(1)}`)
            .click().should("have.attr", "aria-checked", "true");
    }
}
