// Keep DOM selectors in one place for the security-page steps.
export const patElement = (testId: string) => cy.get(`[data-testid="${testId}"]`);

export const patCountBadge = () => patElement("security-pat-count").should("be.visible");

// Exclude the screenreader label when reading the displayed token count.
export function readPATCount(badge: JQuery<HTMLElement>): number {
    const visibleBadge = badge.clone();
    visibleBadge.find(".sr-only").remove();
    const text = visibleBadge.text().trim();
    expect(text, "displayed PAT count").to.match(/^\d+$/);
    return Number(text);
}


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

// Native radios are visually hidden; click the label like a user would.
function checkPATRadio(testId: string): void {
    patElement(testId).parent("label").click();
    patElement(testId).should("be.checked");
}

export function selectPATScope(scope: string): void {
    if (scope === "Alle Dogus") {
        checkPATRadio("security-pat-all-dogus");
    } else {
        expect(scope, "single dogu scope").to.match(/^\/[^/]+$/);
        checkPATRadio("security-pat-selected-dogus");
        patElement(`security-pat-dogu-${scope.slice(1)}`)
            .click().should("have.attr", "aria-checked", "true");
    }
}
