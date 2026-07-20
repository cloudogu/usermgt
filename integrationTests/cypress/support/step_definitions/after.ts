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

function deleteFirstImportRowIfPresent() {
    // wait for the table to settle (loading skeleton/pending refetch) before counting rows,
    // otherwise a stale snapshot can either overcount (skeleton rows) or undercount (pre-refetch)
    cy.wait(500)
    cy.get('body').then(($body) => {
        if ($body.find('tbody tr').length > 0) {
            cy.get('tbody').find('tr:nth-of-type(1)').invoke('find', "td:nth-of-type(4)").find('button').click()
            cy.get('div').find('span').contains("Delete").click()
            cy.get('button').find('span').contains("OK").click()
            deleteFirstImportRowIfPresent()
        }
    })
}

After({tags: "@clean_user_import"}, () => {
    cy.withUser("manager").then(userData => {
        cy.login(userData.username, userData.password, 0)
        cy.visit('/usermgt/summaries')
        cy.clickWarpMenuCheckboxIfPossible()
        deleteFirstImportRowIfPresent()
    })

})

cy.on('after:run', () => {
    cy.usermgtCleanupTestUsers();
    cy.usermgtCleanupTestGroups();
    cy.usermgtTryDeleteUser("testuser_new");
    cy.usermgtTryDeleteUser("newuser");
})
