import 'cypress-plugin-api';

const {
    When
} = require("cypress-cucumber-preprocessor/steps");

const env = require('@cloudogu/dogu-integration-test-library/lib/environment_variables')

//Implement all necessary steps fore dogu integration test library
When("the user clicks the dogu logout button", function () {
    cy.get('#logout').click()
});

When("the user opens the users page", function () {
    cy.visit('/usermgt/#/users')
    cy.clickWarpMenuCheckboxIfPossible()
})

When("the user clicks on the create button", function () {
    cy.get('a[href*="#/user/"]').click()
})

When("the user waits a few seconds", function () {
    cy.wait(1000)
})

When("the user clicks the edit function in his own user entry", function () {

    cy.fixture("testuser_data").then(function (testUser) {
        cy.get("tr").filter(`:contains("${testUser.username}")`).within((tr) => {
            cy.get('.element-interactions button').first().click()
        })
    })

})

When("the user fills the form to create a new user", function () {
    cy.fixture("newuser_data").then(function (newUser) {
        cy.get('#username').type(newUser.username)
        cy.get('#givenname').type(newUser.givenname)
        cy.get('#surname').type(newUser.surname)
        cy.get('#displayName').type(newUser.displayName)
        cy.get('#email').type(newUser.mail)
        cy.get('#password').type(newUser.password)
        cy.get('#confirmPassword').type(newUser.password)
    })
})

When("the user enables the password reset flag", function () {
    cy.get('#pwdResetAtFirstLogin').click()
})

When("the user clicks save", function () {
    cy.get('button[type*="submit"]').click()
})

When("the newly created user logs in", function () {
    cy.fixture("newuser_data").then(function (newUser) {
        cy.login(newUser.username, newUser.password, env.GetMaxRetryCount()) // We dont want to retry the login
    })
})

When("the user opens his own page in usermgt", function () {
    cy.visit('/usermgt/#/account')
    cy.clickWarpMenuCheckboxIfPossible()
})

When("the user deletes his password input", function () {
    cy.get('input[id="password"]').clear()
})

When("the user enters a valid password", function () {
    cy.fixture("newuser_data").then(function (newUser) {
        cy.get('input[id="password"]').type(newUser.password)
    })
})

When("the user {string} sends the upload request with {int} users", (username, userCount) => {
    cy.withUser(username).then(userData => {
        cy.withImportData(userCount).then(importData => {
            cy.logout()
            cy.api({
                method: "POST",
                url: Cypress.config().baseUrl + "/usermgt/api/users/import",
                auth: {
                    'user': userData.username,
                    'pass': userData.password
                },
                failOnStatusCode: false,
                body: importData.data
            }).then((response) => {
                cy.wrap(response.status).as("responseStatus")
            })
        })
    });
})

When(`the user {string} sends the upload request, but is not allowed to`, function (username) {
    cy.fixture(`userdata-${username}`).then(function (newUser) {
        cy.clearCookies();
        cy.api({
            method: "POST",
            url: Cypress.config().baseUrl + "/usermgt/api/users/import",
            auth: {
                'user': newUser.username,
                'pass': newUser.password
            },
            body: "Username;FirstName;Surname;DisplayName;EMail;Group\n" +
                "Tester2;Tes;Ter;Tester2;test2@test.com;exist",
            failOnStatusCode: false,
        }).then((response) => {
            expect(response.status).to.eq(401)
        })
    })
})