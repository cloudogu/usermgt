import '@bahmutov/cy-api'
import {Then} from "@badeball/cypress-cucumber-preprocessor";
import env from "@cloudogu/dogu-integration-test-library/lib/environment_variables";

Then("the newly created user is asked to change his password", function () {
    cy.get('div[data-testid="login-reset-pw-msg"]').should('be.visible')
    cy.get('input[data-testid="password-input"]').should('be.visible')
    cy.get('input[data-testid="confirmedPassword-input"]').should('be.visible')
});

Then("the password reset flag is not visible", function () {
    cy.get('label[data-testid="pwdReset-label"]').should('not.exist')
})

Then("the password reset flag is unchecked", function () {
    cy.get('label[data-testid="pwdReset-label"]').should('be.visible')
    cy.get('input[data-testid="pwdReset-checkbox"]').should('not.be.checked')
})

Then("the user has no administrator privileges in the dogu", function () {
    //not possible for usermgt as there is no distinction between normal user and admin
    //only the CES manager has extended permissions, not the admin
});

Then("the user has administrator privileges in the dogu", function () {
    //not possible for usermgt as there is no distinction between normal user and admin
    //only the CES manager has extended permissions, not the admin
});

Then("the password entry is marked as invalid", function () {
    // Since the validation is only carried out when the text field loses its focus,
    // the change of focus is effected by clicking on another position.
    cy.get('input[data-testid="confirmPassword-input"]').click()
    cy.get('input[data-testid="password-input"]').should('have.class', 'border-textfield-danger-border')
    cy.get('div[data-testid="password-input-error-errors"]').should('be.visible')
});

Then("the password entry is marked as valid", function () {
    // Since the validation is only carried out when the text field loses its focus,
    // the change of focus is effected by clicking on another position.
    cy.get('input[data-testid="confirmPassword-input"]').click()
    cy.get('input[data-testid="password-input"]').should('not.have.class', 'border-textfield-danger-border')
    cy.get('div[data-testid="password-input-error-errors"]').should('not.exist')
});

Then("all password rules are displayed", function () {
    cy.get('span[data-testid="password-input-error-0"]').should('be.visible')
    cy.get('span[data-testid="password-input-error-1"]').should('be.visible')
    cy.get('span[data-testid="password-input-error-2"]').should('be.visible')
    cy.get('span[data-testid="password-input-error-3"]').should('be.visible')
    cy.get('span[data-testid="password-input-error-4"]').should('be.visible')
    cy.get('span[data-testid="password-input-error-5"]').should('be.visible')

    cy.get('div[data-testid="password-input-error-errors"]').contains('The password must contain at least 14 characters.')
    cy.get('div[data-testid="password-input-error-errors"]').contains('The password must contain at least one capital letter.')
    cy.get('div[data-testid="password-input-error-errors"]').contains('The password must contain at least one lower case letter.')
    cy.get('div[data-testid="password-input-error-errors"]').contains('The password must contain at least 1 number.')
    cy.get('div[data-testid="password-input-error-errors"]').contains('The password must contain at least 1 special character.')
    cy.get('div[data-testid="password-input-error-errors"]').contains('The password must not contain only spaces.')
});

Then("the password-confirm entry is marked as invalid", function () {
    // Since the validation is only carried out when the text field loses its focus,
    // the change of focus is effected by clicking on another position.
    cy.get('input[data-testid="password-input"]').click()
    cy.get('input[data-testid="confirmPassword-input"]').should('have.class', 'border-textfield-danger-border')
    cy.get('div[data-testid="confirmPassword-input-error-errors"]').should('be.visible')
});

Then("the password-confirm entry is marked as valid", function () {
    // Since the validation is only carried out when the text field loses its focus,
    // the change of focus is effected by clicking on another position.
    cy.get('input[data-testid="password-input"]').click()
    cy.get('input[data-testid="confirmPassword-input"]').should('not.have.class', 'border-textfield-danger-border')
    cy.get('div[data-testid="confirmPassword-input-error-errors"]').should('not.exist')
});

Then("the password-confirm rules are displayed", function () {
    cy.get('span[data-testid="confirmPassword-input-error-0"]').should('be.visible')
    cy.get('div[data-testid="confirmPassword-input-error-errors"]').contains('Passwords must match.')
});

Then("the user {string} was created",function (username:string) {
    cy.api({
        method: "GET",
        url: Cypress.config().baseUrl + "/usermgt/api/users/" + username,
        auth: {
            'user': env.GetAdminUsername(),
            'pass': env.GetAdminPassword()
        },
    }).then((response) => {
        expect(response.status).to.eq(200)
    })
})

Then("the user {string} does not exists",function (username:string) {
    cy.clearCookies();
    cy.request({
        method: "GET",
        url: Cypress.config().baseUrl + "/usermgt/api/users/" + username,
        auth: {
            'user': env.GetAdminUsername(),
            'pass': env.GetAdminPassword()
        },
        failOnStatusCode: false,
    }).then((response) => {
        expect(response.status).to.eq(404)
    })
})

Then("the users-page is shown", function () {
    cy.get('h1').contains("Users")
    cy.get('table[data-testid="users-table"]').should('be.visible')
    cy.get('button[data-testid="user-create"]').contains('Create user')
    cy.get('form[data-testid="users-filter"]').should('be.visible')
});

Then("the users-page contains the user {string}", function (username:string) {
    cy.get(`tr[data-testid="users-row-${username}"]`).as('row');
    cy.get('@row').should('be.visible');
    cy.get('@row').find("td").should('have.length', 4);
    cy.get('@row').find("td:nth-of-type(1)").contains(username);
    cy.get('@row').find("td:nth-of-type(4)").find(`a[id="${username}-edit-link"]`).should('be.visible');
    cy.get('@row').find("td:nth-of-type(4)").find(`button[id="${username}-delete-button"]`).should('be.visible');
});

Then("the users-page contains the displayName {string}", function (displayName:string) {
    cy.get('table[data-testid="users-table"]')
        .find('tr td:nth-of-type(2)').filter(`:contains("${displayName}")`).parent().as('row');
    cy.get('@row').should('be.visible');
    cy.get('@row').find("td").should('have.length', 4);
    cy.get('@row').find("td:nth-of-type(2)").contains(displayName);
});


Then("the users-page contains at least {string} users", function (userCountNum: string) {
    const userCount = parseInt(userCountNum);
    cy.get('table[data-testid="users-table"] tbody tr').should('have.length.gte', userCount);
});

Then("the users-page contains exactly {string} users", function (userCountNum: string) {
    const userCount = parseInt(userCountNum);
    cy.get('.animate-spin').should('not.exist');
    cy.get('table[data-testid="users-table"] tbody tr').should('have.length', userCount);
});

Then("the new-user-page is shown", function () {
    cy.get('h1').contains("New user")
    cy.get('input[data-testid="username-input"]').should('be.visible')
    cy.get('input[data-testid="givenname-input"]').should('be.visible')
    cy.get('input[data-testid="surname-input"]').should('be.visible')
    cy.get('input[data-testid="displayName-input"]').should('be.visible')
    cy.get('input[data-testid="mail-input"]').should('be.visible')
    cy.get('input[data-testid="password-input"]').should('be.visible')
    cy.get('input[data-testid="confirmPassword-input"]').should('be.visible')
    cy.get('div[data-testid="groups"]').should('be.visible')
    cy.get('button[data-testid="save-button"]').should('be.visible')
    cy.get('button[data-testid="back-button"]').should('be.visible')
});

Then("the username-field is marked as invalid", function () {
    // Since the validation is only carried out when the text field loses its focus,
    // the change of focus is effected by clicking on another position.
    cy.get('h1').click()
    cy.get('input[data-testid="username-input"]').should('have.class', 'border-textfield-danger-border')
    cy.get('div[data-testid="username-input-error-errors"]').should('be.visible')
    cy.get('div[data-testid="username-input-error-errors"]').contains('Must contain at least 2 characters')
});

Then("an user-exists-error is shown an the fields are marked invalid", function () {
    cy.get('.border-alert-danger-inverse-border').should('be.visible').as("notification");
    cy.get('@notification').contains(/A user with the username .* already exists./);
    cy.get('@notification').contains(/A user with the E-Mail .* already exists./);
    cy.get('input[data-testid="username-input"]').should('have.class', 'border-textfield-danger-border')
    cy.get('input[data-testid="mail-input"]').should('have.class', 'border-textfield-danger-border')
});

Then("a group named {string} is assigned to the user", function (group: string) {
    cy.get('table[data-testid="groups-table"] tbody tr').contains(group);
})

Then("the edit-user-page for user {string} is shown", function (name: string) {
    cy.get('h1').contains("Edit user")
    cy.get('input[data-testid="username-input"]').should('have.value', name)
    cy.get('input[data-testid="username-input"]').should('be.disabled')
    cy.get('input[data-testid="givenname-input"]').should('be.visible')
    cy.get('input[data-testid="surname-input"]').should('be.visible')
    cy.get('input[data-testid="displayName-input"]').should('be.visible')
    cy.get('input[data-testid="mail-input"]').should('be.visible')
    cy.get('input[data-testid="password-input"]').should('be.visible')
    cy.get('input[data-testid="confirmPassword-input"]').should('be.visible')
    cy.get('div[data-testid="groups"]').should('be.visible')
    cy.get('button[data-testid="save-button"]').should('be.visible')
    cy.get('button[data-testid="back-button"]').should('be.visible')
});

Then("the user has no groups", function () {
    cy.get('table[data-testid="groups-table"] tbody tr').contains("No groups assigned");
})

/* GROUPS */

Then("the groups-page is shown", function () {
    cy.get('h1').contains("Groups")
    cy.get('table[data-testid="groups-table"]').should('be.visible')
    cy.get('button[data-testid="group-create"]').contains('Create Group')
    cy.get('form[data-testid="groups-filter"]').should('be.visible')
});

Then("the groups-page contains the group {string}", function (groupName:string) {
    cy.get(`tr[data-testid="groups-row-${groupName}"]`).as('row');
    cy.get('@row').should('be.visible');
    cy.get('@row').find("td").should('have.length', 4);
    cy.get('@row').find("td:nth-of-type(1)").contains(groupName);
    cy.get('@row').find("td:nth-of-type(4)").find(`a[id="${groupName}-edit-link"]`).should('be.visible');
    cy.get('@row').find("td:nth-of-type(4)").find(`button[id="${groupName}-delete-button"]`).should('be.visible');
});

Then("the groups-page contains the group-description {string}", function (description:string) {
    cy.get('table[data-testid="groups-table"]')
        .find('tr td:nth-of-type(2)').filter(`:contains("${description}")`).parent().as('row');
    cy.get('@row').should('be.visible');
    cy.get('@row').find("td").should('have.length', 4);
    cy.get('@row').find("td:nth-of-type(2)").contains(description);
});

Then("the groups-page contains at least {string} groups", function (groupCountNum: string) {
    const groupCount = parseInt(groupCountNum);
    cy.get('table[data-testid="groups-table"] tbody tr').should('have.length.gte', groupCount);
});

Then("the groups-page contains exactly {string} groups", function (groupCountNum: string) {
    const groupCount = parseInt(groupCountNum);
    cy.get('.animate-spin').should('not.exist');
    cy.get('table[data-testid="groups-table"] tbody tr').should('have.length', groupCount);
});

Then("the new-group-page is shown", function () {
    cy.get('h1').contains("New Group")
    cy.get('input[data-testid="name-input"]').should('be.visible')
    cy.get('textarea[data-testid="description-area"]').should('be.visible')
    cy.get('div[data-testid="members"]').should('be.visible')
    cy.get('button[data-testid="save-button"]').should('be.visible')
    cy.get('button[data-testid="back-button"]').should('be.visible')
});

Then("the group-name-field is marked as invalid", function () {
    // Since the validation is only carried out when the text field loses its focus,
    // the change of focus is effected by clicking on another position.
    cy.get('h1').click()
    cy.get('input[data-testid="name-input"]').should('have.class', 'border-textfield-danger-border')
    cy.get('div[data-testid="name-input-error-errors"]').should('be.visible')
    cy.get('div[data-testid="name-input-error-errors"]').contains('Minimum length is 2 characters')
});

Then("a user named {string} is member of the group", function (username: string) {
    cy.get('table[data-testid="members-table"] tbody tr').contains(username);
})

Then("the group has {string} members", function (memberNum: string) {
    const memberCount = parseInt(memberNum);
    cy.get('table[data-testid="members-table"] tbody tr').should('have.length', memberCount);
})

Then("the group has no members", function () {
    cy.get('table[data-testid="members-table"] tbody tr').contains("No members assigned");
})

Then("the edit-group-page for group {string} is shown", function (name: string) {
    cy.get('h1').contains("Edit group")
    cy.get('input[data-testid="name-input"]').should('have.value', name)
    cy.get('input[data-testid="name-input"]').should('be.disabled')
    cy.get('textarea[data-testid="description-area"]').should('be.visible')
    cy.get('div[data-testid="members"]').should('be.visible')
    cy.get('button[data-testid="save-button"]').should('be.visible')
    cy.get('button[data-testid="back-button"]').should('be.visible')
});

Then("a success alert will be shown containing the text {string}", function (name: string) {
    cy.get('.border-alert-primary-inverse-border').should('be.visible');
    cy.get('.border-alert-primary-inverse-border').contains(name);
});

Then("an access denied message will be shown", function () {
    cy.get('h1[data-testid="access-denied-message"]').should('be.visible');
});

Then("users, groups, user import and import overview should not be visible in the navbar", function () {
    cy.get('li[data-testid="cloudogu-navbar-li-/users"]').should('not.exist')
    cy.get('li[data-testid="cloudogu-navbar-li-/groups"]').should('not.exist')
    cy.get('li[data-testid="cloudogu-navbar-li-/users/import"]').should('not.exist')
    cy.get('li[data-testid="cloudogu-navbar-li-/summaries"]').should('not.exist')
});

Then("users, groups, user import and import overview should be visible in the navbar", function () {
    cy.get('li[data-testid="cloudogu-navbar-li-/users"]').should('be.visible')
    cy.get('li[data-testid="cloudogu-navbar-li-/groups"]').should('be.visible')
    cy.get('li[data-testid="cloudogu-navbar-li-/users/import"]').should('be.visible')
    cy.get('li[data-testid="cloudogu-navbar-li-/summaries"]').should('be.visible')
});

Then("the account page for user {string} is shown", function (username: string)  {
    cy.get('h1').contains("Account")
    cy.get('input[data-testid="username-input"]').should('have.value', username)
});

Then("the user import page is shown", function () {
    cy.get('h1').contains("User import")
    cy.get('p').invoke('find','a').invoke('attr', 'href').should('match', /(https:\/\/docs\.cloudogu\.com\/en\/usermanual\/usermanagement\/)|(https:\/\/docs\.cloudogu\.com\/de\/usermanual\/usermanagement\/)/)
    cy.get('input[data-testid="userImport-input"]').should('be.visible')
    cy.get('button[data-testid="upload-button"]').should('be.visible')
    cy.get('button[data-testid="upload-button"]').should('be.disabled')
    cy.get('button[data-testid="reset-button"]').should('be.visible')
    cy.get('button[data-testid="reset-button"]').should('be.disabled')
})

Then("a table of the file content for the file {string} is displayed", function (filename: string) {
    cy.get('h2').contains("Content of the CSV file")
    cy.get('table').should('be.visible')
    cy.get('tr').as('row')
    cy.get('@row').should('be.visible')
    cy.get('@row').find("td").should('have.length', 7)

    cy.fixture(filename).then(data => data.split('\n')).then((data) => {
        let userInfo = data[1].split(',')
        cy.get('@row').find("td:nth-of-type(1)").contains(userInfo[0])
        cy.get('@row').find("td:nth-of-type(2)").contains(userInfo[1])
        cy.get('@row').find("td:nth-of-type(3)").contains(userInfo[2])
        cy.get('@row').find("td:nth-of-type(4)").contains(userInfo[3])
        cy.get('@row').find("td:nth-of-type(5)").contains(userInfo[4])
        cy.get('@row').find("td:nth-of-type(6)").contains(userInfo[5])
        cy.get('@row').find("td:nth-of-type(7)").contains(userInfo[6])
    })
    cy.get('button[data-testid="upload-button"]').should('be.visible').and('not.be.disabled')
    cy.get('button[data-testid="reset-button"]').should('be.visible').and('not.be.disabled')
})

Then("the user import page shows a failed import", function () {

    cy.get('h1').contains("Userimport")
    cy.get('p[data-testid="import-status-message"]').contains("Import failed!")
    cy.get('details[data-testid="failed-import-details"]').invoke('find', 'summary').invoke('attr', 'text').contains("Skipped data rows (1)")
    cy.get('details[data-testid="failed-import-details"]').invoke('attr', 'open').should('not.exist')
    cy.get('p[data-testid="import-download-link"]').invoke('find', 'a').contains("Download import overview")
})

Then("the import result is downloaded and contains error information regarding the file {string}", function (fileName: string) {
    // To test the download, the generated import ID is extracted from the URL
    cy.url()
        .then(url => {
            let urlSplit = url.split("/");
            let fileId = urlSplit[6];
            cy.readFile("cypress/downloads/" + fileId + ".json").then((fileContent) => {
                expect(fileContent.importID).to.eq(fileId)
                expect(fileContent.filename).to.eq(fileName)
                expect(fileContent.timestamp).contains(/[0-9]+/)
                expect(fileContent.created).is.empty
                expect(fileContent.updated).is.empty
                expect(fileContent.errors[0].lineNumber).to.eq(2)
                expect(fileContent.errors[0].message).to.eq("entity is not valid")
                expect(fileContent.errors[0].params.columns[0]).to.eq("username")
                expect(fileContent.summary.importID).to.eq(fileId)
                expect(fileContent.summary.filename).to.eq(fileName)
                expect(fileContent.summary.timestamp).contains(/[0-9]+/)
                expect(fileContent.summary.summary.created).to.eq(0)
                expect(fileContent.summary.summary.updated).to.eq(0)
                expect(fileContent.summary.summary.skipped).to.eq(1)
            })
        })
})

Then("the table shows that the username was not in the correct format", function () {
    cy.get('details[data-testid="failed-import-details"]').invoke('attr', 'open').should('exist')
    cy.get('details[data-testid="failed-import-details"]').invoke('find', 'table').should('be.visible')
    cy.get('tr').as('row')
    cy.get('@row').should('be.visible')
    cy.get('@row').find("td:nth-of-type(2)").contains("'username'")
})

Then("the new user {string} was not added", function (username: string) {
    cy.get('table').should('be.visible')
    cy.get('tr').as('row')
    cy.get('@row').should('be.visible')
    cy.get('@row').invoke('text').should('not.equal', username)
})

Then("a table with the import information regarding the file {string} is shown", function (fileName: string) {
    cy.get('h1').contains("Import overviews")
    cy.get('table').should('be.visible')
    cy.get('tr').as('row')
    cy.get('@row').should('be.visible')
    cy.get('@row').find("td:nth-of-type(1)").contains(fileName)
    //cy.get('@row').find("td:nth-of-type(3)").contains("New: 0, Updated: 0, Skipped: 1")
})

Then("no content is displayed and upload is not possible", function () {
    cy.get('h2').should('not.exist')
    cy.get('table').should('not.exist')
    cy.get('button[data-testid="upload-button"]').should('be.visible').and('be.disabled')
    cy.get('button[data-testid="reset-button"]').should('be.visible').and('be.disabled')
})

Then("the user import page shows a successful import", function () {
    cy.get('h1').contains("Userimport")
    cy.get('p[data-testid="import-status-message"]').contains("Import successfully completed!")
    cy.get('details[data-testid="created-import-details"]').invoke('find', 'summary').invoke('attr', 'text').contains("Created accounts (1)")
    cy.get('details[data-testid="created-import-details"]').invoke('attr', 'open').should('not.exist')
    cy.get('p[data-testid="import-download-link"]').invoke('find', 'a').contains("Download import overview")
})

Then("the table shows the information about the user {string}", function (username: string) {
    cy.get('details[data-testid="created-import-details"]').invoke('attr', 'open').should('exist')
    cy.get('details[data-testid="created-import-details"]').invoke('find', 'table').should('be.visible')
    cy.get('tr').as('row')
    cy.get('@row').should('be.visible')
    cy.get('@row').find("td:nth-of-type(1)").contains(username)
})

Then("the import result is downloaded and contains information regarding the file {string}", function (fileName: string) {
    // To test the download, the generated import ID is extracted from the URL
    cy.url()
        .then(url => {
            let urlSplit = url.split("/");
            let fileId = urlSplit[6];
            cy.readFile("cypress/downloads/" + fileId + ".json").then((fileContent) => {
                expect(fileContent.importID).to.eq(fileId)
                expect(fileContent.filename).to.eq(fileName)
                expect(fileContent.timestamp).contains(/[0-9]+/)
                expect(fileContent.created).is.not.empty
                expect(fileContent.updated).is.empty
                expect(fileContent.errors).is.empty
                expect(fileContent.summary.importID).to.eq(fileId)
                expect(fileContent.summary.filename).to.eq(fileName)
                expect(fileContent.summary.timestamp).contains(/[0-9]+/)
                expect(fileContent.summary.summary.created).to.eq(1)
                expect(fileContent.summary.summary.updated).to.eq(0)
                expect(fileContent.summary.summary.skipped).to.eq(0)
            })
        })
})

Then("the new user {string} was added", function (username: string) {
    cy.get('table').should('be.visible')
    cy.get('tr').as('row')
    cy.get('@row').should('be.visible')
    cy.get('@row').find("td:nth-of-type(1)").contains(username)
})

Then("the password reset flag is checked", function () {
    cy.get('label[data-testid="pwdReset-label"]').should('be.visible')
    cy.get('input[data-testid="pwdReset-checkbox"]').should('be.checked')
})
