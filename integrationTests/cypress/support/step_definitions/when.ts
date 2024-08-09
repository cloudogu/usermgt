import '@bahmutov/cy-api'
import { When } from "@badeball/cypress-cucumber-preprocessor";
import env from "@cloudogu/dogu-integration-test-library/lib/environment_variables";

//Implement all necessary steps fore dogu integration test library
When("the user clicks the dogu logout button", function () {
    cy.get('#logout').click()
});

When("the user opens the users page", function () {
    cy.visit('/usermgt/users')
    cy.clickWarpMenuCheckboxIfPossible()
})

When("the user selects the next users-page", function () {
    cy.get(`button[data-testid="users-pagination-forward"]`).click();
})

When("the user sets the users-filter to {string}", function (filter: string) {
    cy.get(`input[data-testid="users-filter-input"]`).clear().type(filter);
    cy.get(`button[data-testid="users-filter-button"]`).click();
})

When("the user clears the users-filter", function () {
    cy.get(`input[data-testid="users-filter-input"]`).clear();
    cy.get(`button[data-testid="users-filter-button"]`).click();
})

When("the user clicks on the create-user button", function () {
    cy.get('button[data-testid="user-create"]').click()
})

When("the user waits a few seconds", function () {
    cy.wait(1000)
})

When("the user fills the user-form for a user with the name {string}", function (username: string) {
    cy.fixture("newuser_data").then(function (newUser) {
        cy.get('input[data-testid="username-input"]').type(username);
        cy.get('input[data-testid="givenname-input"]').type(newUser.givenname);
        cy.get('input[data-testid="surname-input"]').type(newUser.surname)
        cy.get('input[data-testid="displayName-input"]').type(newUser.displayName)
        cy.get('input[data-testid="mail-input"]').type(newUser.mail)
        cy.get('input[data-testid="password-input"]').type(newUser.password)
        cy.get('input[data-testid="confirmPassword-input"]').type(newUser.password)
    });
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

When("the user deletes his given name input", function () {
    cy.get('input[id="givenname"]').clear()
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

When("the user adds the group {string} to the user", function (group: string) {
    cy.get('input[data-testid="groups-searchbar-input"]').type(group);
    cy.get('li[data-testid="groups-searchbar-li-0"] button').click();
    cy.get('table[data-testid="groups-table"] tbody tr').contains(group);
})

When("the user clicks on the edit-user button for the user {string}", function (name) {
    cy.get(`a[id="${name}-edit-link"]`).click();
})

When("the user clicks on the delete-user button for the user {string}", function (name) {
    cy.get(`button[id="${name}-delete-button"]`).click();
})

When("the user edits the user-displayName to {string}", function (displayName: string) {
    cy.get('input[data-testid="displayName-input"]').clear().type(displayName);
})

When("the user removes the group {string} from the user", function (group: string) {
    cy.get('table[data-testid="groups-table"] tbody tr').filter(`:contains("${group}")`)
        .find('td:nth-of-type(2) button').click();

    cy.get('dialog[data-testid="remove-group-dialog"]').as('dialog');
    cy.get('@dialog').should('be.visible');
    cy.get('@dialog').find('h3').contains('Remove group assigment');
    cy.get('@dialog').find('button:nth-of-type(1)').click();
})

When("the user confirms the delete-user-confirmation-dialog", function () {
    cy.get('div[data-testid="user-delete-dialog-content"]').as('dialog');
    cy.get('@dialog').should('be.visible');
    cy.get('@dialog').find('h2').contains('Delete user');
    cy.get('@dialog').find('button[data-testid="user-delete-dialog-confirm-btn"]').click();
})

/* GROUPS */

When("the user opens the groups page", function () {
    cy.visit('/usermgt/groups')
    cy.clickWarpMenuCheckboxIfPossible()
})

When("the user selects the next groups-page", function () {
    cy.get(`button[data-testid="groups-pagination-forward"]`).click();
})

When("the user sets the groups-filter to {string}", function (filter: string) {
    cy.get(`input[data-testid="groups-filter-input"]`).clear().type(filter);
    cy.get(`button[data-testid="groups-filter-button"]`).click();
})

When("the user clears the groups-filter", function () {
    cy.get(`input[data-testid="groups-filter-input"]`).clear();
    cy.get(`button[data-testid="groups-filter-button"]`).click();
})

When("the user clicks on the create-group button", function () {
    cy.get('button[data-testid="group-create"]').click();
})

When("the user clicks on the edit-group button for the group {string}", function (name) {
    cy.get(`a[id="${name}-edit-link"]`).click();
})

When("the user clicks on the delete-group button for the group {string}", function (name) {
    cy.get(`button[id="${name}-delete-button"]`).click();
})

When("the user fills the group-form for a group with the name {string}", function (name: string) {
    cy.get('input[data-testid="name-input"]').type(name);
    cy.get('textarea[data-testid="description-area"]').type(`Description for group ${name}`);
})

When("the user edits the group-description to {string}", function (description: string) {
    cy.get('textarea[data-testid="description-area"]').clear().type(description);
})

When("the user submits the group-form", function () {
    cy.get('button[data-testid="save-button"]').click();
})

When("the user adds the member {string} to the group", function (username: string) {
    cy.get('input[data-testid="members-searchbar-input"]').type(username);
    cy.get('li[data-testid="members-searchbar-li-0"] button').click();
    cy.get('table[data-testid="members-table"] tbody tr').contains(username);
})

When("the user removes the member {string} from the group", function (username: string) {
    cy.get('table[data-testid="members-table"] tbody tr').filter(`:contains("${username}")`)
        .find('td:nth-of-type(2) button').click();

    cy.get('dialog[data-testid="remove-member-dialog"]').as('dialog');
    cy.get('@dialog').should('be.visible');
    cy.get('@dialog').find('h3').contains('Remove member');
    cy.get('@dialog').find('button:nth-of-type(1)').click();
})

When("the user confirms the delete-group-confirmation-dialog", function () {
    cy.get('div[data-testid="group-delete-dialog-content"]').as('dialog');
    cy.get('@dialog').should('be.visible');
    cy.get('@dialog').find('h2').contains('Delete group');
    cy.get('@dialog').find('button[data-testid="group-delete-dialog-confirm-btn"]').click();
})

/* IMPORT */

When("the user opens the user import page", function () {
    cy.visit('/usermgt/users/import')
    cy.clickWarpMenuCheckboxIfPossible()
})

When("the user opens the user import summaries page", function () {
    cy.visit('/usermgt/summaries')
    cy.clickWarpMenuCheckboxIfPossible()
})

When("the user opens the user import summary details page", function (summaryId: string) {
    cy.visit(`/usermgt/users/import/${summaryId}`)
    cy.clickWarpMenuCheckboxIfPossible()
})

When("the user selects the file {string}", function (fileName: string) {
    cy.get('input[type="file"]').selectFile("cypress/fixtures/" + fileName)
})

When("the user uploads the file {string}", function (fileName: string) {
    cy.get('input[type="file"]').selectFile("cypress/fixtures/" + fileName)
    cy.get('button[data-testid="upload-button"]').click()
})

When("the user clicks on the line 'Skipped data rows'", function () {
    cy.get('details[data-testid="failed-import-details"]').click()
})

When("the user downloads the import overview", function () {
    cy.get('p[data-testid="import-download-link"]').invoke('find', 'a').click()
})

When("the user opens the user import details page", function () {
    cy.visit('/usermgt/summaries')
    cy.clickWarpMenuCheckboxIfPossible()
    if(cy.get('tbody').find('tr:nth-of-type(1)').invoke('find',"td:nth-of-type(4)").should('exist')) {
        cy.get('tbody').find('tr:nth-of-type(1)').invoke('find', "td:nth-of-type(4)").find('button').click()
        cy.get('div').find('span').contains("Details").click()
    }
})
