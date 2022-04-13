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
})

When("the user clicks the edit function in his own user entry", function () {
 cy.get("tr").filter(`:contains("${env.GetAdminUsername()}")`).within((tr) => {
    console.log(tr)
     cy.get('.element-interactions button').first().click()
 })
// ('button[title="edit"]').click()
})

When("the user opens and fills the form to create a new user", function () {
    cy.get('a[href*="#/user/"]').click()
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
