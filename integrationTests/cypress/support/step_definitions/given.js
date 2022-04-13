const {
    Given
} = require("cypress-cucumber-preprocessor/steps");

const env = require('@cloudogu/dogu-integration-test-library/lib/environment_variables')

// Given("the user is logged into the CES", function () {
//     cy.login(env.GetAdminUsername(), env.GetAdminPassword())
// })