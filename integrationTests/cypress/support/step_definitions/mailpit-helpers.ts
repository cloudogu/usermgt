export function getLatestMailBodyForRecipient(recipient: string): Cypress.Chainable<string> {
    return cy.mailpitHasEmailsByTo(recipient)
        .then(({messages}) => cy.mailpitGetMail(messages[0].ID))
        .mailpitGetMailTextBody();
}
