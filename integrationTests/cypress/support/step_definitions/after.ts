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

After({tags: "@clean_user_import"}, () => {
    cy.withUser("manager").then(userData => {
        cy.login(userData.username, userData.password, 0)
        cy.visit('/usermgt/summaries')
        cy.clickWarpMenuCheckboxIfPossible()
        cy.get('table').find('tr').then((row) => {
            let i: number = 1;
            for (i; i < row.length; i++){
                cy.get('tbody').find('tr:nth-of-type(1)').invoke('find',"td:nth-of-type(4)").find( 'button').click()
                cy.get('div').find('span').contains("Delete").click()
                cy.get('button').find('span').contains("OK").click()
            }
        })
    })

})

cy.on('after:run', () => {
    cy.usermgtCleanupTestUsers();
    cy.usermgtCleanupTestGroups();
    cy.usermgtTryDeleteUser("testuser_new");
    cy.usermgtTryDeleteUser("newuser");
})
