import {After, Before} from "@badeball/cypress-cucumber-preprocessor";

After({tags: "@requires_new_user"}, () => {
    cy.logout();

    cy.fixture("newuser_data").then(function (newUser) {
        cy.log("Removing new user")
        cy.usermgtDeleteUser(newUser.username)
        cy.deleteUserFromDoguViaAPI(newUser.username, false)
    })

    cy.clearCookies()
});


After({tags: "@clean_after"}, () => {
    cy.usermgtCleanupTestUsers();
    cy.usermgtCleanupTestGroups();
})

After({tags: "@reduce_group_env"},() => {
    Cypress.env("groups", Cypress.env("groups") - 1)
})
After({tags: "@reduce_user_env"},() => {
    Cypress.env("users", Cypress.env("users") - 1)
})

cy.on('after:run', () => {
    cy.usermgtCleanupTestUsers();
    cy.usermgtCleanupTestGroups();
    cy.usermgtTryDeleteUser("testuser_new");
    cy.usermgtTryDeleteUser("newuser");
})