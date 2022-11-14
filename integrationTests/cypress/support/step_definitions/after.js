const {
    After,
} = require("cypress-cucumber-preprocessor/steps");

After({tags: "@requires_new_user"}, () => {
    cy.logout();

    cy.fixture("newuser_data").then(function (newUser) {
        cy.log("Removing new user")
        cy.usermgtDeleteUser(newUser.username)
        cy.deleteUserFromDoguViaAPI(newUser.username, false)
    })

    cy.clearCookies()
});

After(() => {
    cy.usermgtCleanupTestUsers()
})


