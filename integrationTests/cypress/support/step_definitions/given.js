const {Given} = require("cypress-cucumber-preprocessor/steps");

const env = require('@cloudogu/dogu-integration-test-library/lib/environment_variables');

Given("the user {string} exists", (username) => {
    cy.withUser(username).then(userData => {
        cy.log(userData)
        cy.usermgtTryDeleteUser(userData.username);
        cy.usermgtCreateUser(userData.username, userData.givenname, userData.surname, userData.displayName, userData.mail, userData.password, userData.pwdReset, userData.groups);
    });
})

Given("the user {string} is member of the group {string}", function (username, group) {
    cy.api({
        method: "POST",
        url: Cypress.config().baseUrl + "/usermgt/api/groups/" + group + "/members/" + username,
        auth: {
            'user': env.GetAdminUsername(),
            'pass': env.GetAdminPassword()
        }
    }).then((response) => {
        expect(response.status).to.eq(204)
    })
})



