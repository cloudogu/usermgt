import { Given } from "@badeball/cypress-cucumber-preprocessor";
import env from "@cloudogu/dogu-integration-test-library/lib/environment_variables";
Given("the user {string} exists", (username: string) => {
    cy.withUser(username).then(userData => {
        cy.log(userData)
        cy.usermgtTryDeleteUser(userData.username);
        cy.usermgtCreateUser(userData.username, userData.givenname, userData.surname, userData.displayName, userData.mail, userData.password, userData.pwdReset, userData.groups);
    });
})

Given("the group {string} exists", (testGroup: string) => {
    cy.withUser("testuser").then(() => {
        cy.usermgtTryDeleteGroup(testGroup);
        cy.usermgtCreateGroup(testGroup, "")
    });
})

Given("{string} test-users exist", (users: string) => {
    const userCount = parseInt(users);
    cy.withUser("testuser").then(userData => {
        for(let i = Cypress.env("users") + 1; i <= userCount; i++){
            const testUser = {...userData};
            testUser.username += `_${i}`;
            testUser.givenname += `_${i}`;
            testUser.surname += `_${i}`;
            testUser.displayName += ` ${i}`;
            const mailParts = testUser.mail.split("@");
            testUser.mail = `${mailParts[0]}${i}@${mailParts[1]}`;
            cy.usermgtTryDeleteUser(testUser.username);
            cy.usermgtCreateUser(testUser.username, testUser.givenname, testUser.surname, testUser.displayName, testUser.mail, testUser.password, testUser.pwdReset, testUser.groups)
    }
}).then(() => {
    if (userCount - Cypress.env("users") > 0) {
        Cypress.env("users", userCount)
    }
})})

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

Given("the user {string} with password {string} is logged in", function (username: string, password: string){
    cy.clearAllCookies()
    cy.login(username, password, 3);
});

/* GROUPS */

Given("{string} test-groups exist", (groups: string) => {
    const groupCount = parseInt(groups);
    console.log(groupCount)
    cy.withUser("testuser").then(() => {
        for(let i = Cypress.env("groups") + 1; i <= groupCount; i++){
            const testGroup = {
                name: `testGroup_${i}`,
                description: `Test Group ${i}`,
            };
            console.log("testGroup created")

            cy.usermgtTryDeleteGroup(testGroup.name);
            cy.usermgtCreateGroup(testGroup.name, testGroup.description)
        }
    }).then(() => {
        if (groupCount - Cypress.env("groups") > 0) {
            Cypress.env("groups", groupCount);
        }
    })
})