import '@bahmutov/cy-api'
import { When } from "@badeball/cypress-cucumber-preprocessor";
import env from "@cloudogu/dogu-integration-test-library/environment_variables";

//Implement all necessary steps fore dogu integration test library
When("the user clicks the dogu logout button", function () {
    cy.get('#logout').click()
});

When("the user opens the users page", function () {
    cy.visit('/usermgt/users')
    cy.clickWarpMenuCheckboxIfPossible()
})

When("the user selects the {string} users-page", function (pageNum: string) {
    const page = parseInt(pageNum);
    cy.get(`button[data-testid="users-footer-pagination-li-${page}-btn"]`).click();
})
When("the user sets the users-filter to {string}", function (filter: string) {
    cy.get(`input[data-testid="users-filter-input"]`).type(filter);
    cy.get(`button[data-testid="users-filter-button"]`).click();
})

When("the user clears the users-filter", function () {
    cy.get(`input[data-testid="users-filter-input"]`).clear();
    cy.get(`button[data-testid="users-filter-button"]`).click();
})

When("the user clicks on the create button", function () {
    cy.get('button[data-testid="user-create"]').click()
})

When("the user waits a few seconds", function () {
    cy.wait(1000)
})

When("the user clicks the edit function in his own user entry", function () {

    cy.fixture("testuser_data").then(function (testUser) {
        cy.get("tr").filter(`:contains("${testUser.username}")`).within((tr) => {
            cy.get(`a[id="${testUser.username}-edit-link"]`).click()
        })
    })

})

When("the user fills the form to create a new user", function () {
    cy.fixture("newuser_data").then(function (newUser) {
        cy.get('#username').type(newUser.username)
        cy.get('#givenname').type(newUser.givenname)
        cy.get('#surname').type(newUser.surname)
        cy.get('#displayName').type(newUser.displayName)
        cy.get('#mail').type(newUser.mail)
        cy.get('#password').type(newUser.password)
        cy.get('#confirmPassword').type(newUser.password)
    })
})

When("the user enables the password reset flag", function () {
    cy.get('label[data-testid="pwdReset-label"]').click()
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

When("the user enters a valid confirm-password", function () {
    cy.fixture("newuser_data").then(function (newUser) {
        cy.get('input[id="confirmPassword"]').clear().type(newUser.password)
    })
})

When("the user {string} sends the upload request with {int} users", (username: string, userCount: number) => {
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

/* GROUPS */

When("the user opens the groups page", function () {
    cy.visit('/usermgt/groups')
    cy.clickWarpMenuCheckboxIfPossible()
})

When("the user selects the {string} groups-page", function (pageNum: string) {
    const page = parseInt(pageNum);
    cy.get(`button[data-testid="groups-footer-pagination-li-${page}-btn"]`).click();
})
When("the user sets the groups-filter to {string}", function (filter: string) {
    cy.get(`input[data-testid="groups-filter-input"]`).type(filter);
    cy.get(`button[data-testid="groups-filter-button"]`).click();
})

When("the user clears the groups-filter", function () {
    cy.get(`input[data-testid="groups-filter-input"]`).clear();
    cy.get(`button[data-testid="groups-filter-button"]`).click();
})