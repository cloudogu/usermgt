const {
    Given
} = require("cypress-cucumber-preprocessor/steps");

Given("the user is member of the cesManager group", function () {
    cy.fixture("testuser_data").then(function (testUser) {
        cy.promoteAccountToManager(testUser.username)
    })
})