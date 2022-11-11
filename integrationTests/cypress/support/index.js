// Loads all commands from the dogu integration library into this project
const doguTestLibrary = require('@cloudogu/dogu-integration-test-library')
const env = require('@cloudogu/dogu-integration-test-library/lib/environment_variables')

doguTestLibrary.registerCommands()

import "./commands/required_commands_for_dogu_lib"

/**
 * Overwrite logut and login behavior as the session cookie for the usermgt api-requests are not
 * deleted correctly. As such we need to delete them manually as otherwise the user is falsy logged 
 * in and not redirected to `/cas/login`.
 */
const logout = () => {
    cy.api({
        method: "GET",
        url: Cypress.config().baseUrl + "/usermgt/api/logout",
        auth: {
            'user': env.GetAdminUsername(),
            'pass': env.GetAdminPassword()
        }
    });
    cy.clearCookies()
}

const login = (username, password, retryCount = 0) => {
    cy.clearCookies()

    cy.visit("/" + env.GetDoguName(), {failOnStatusCode: false})
    cy.clickWarpMenuCheckboxIfPossible()

    cy.get('input[data-testid="login-username-input-field"]').type(username)
    cy.get('input[data-testid="login-password-input-field"]').type(password)
    cy.get('div[data-testid=login-form-login-button-container]').children('button').click()

    cy.url().then(function (url) {
        if (url.includes("cas/login") && retryCount < env.GetMaxRetryCount()) {
            ++retryCount
            cy.login(username, password, retryCount)
        }
    })
}

function filterByUsername(arr, query) {
    return arr.filter((el) => el.username.toLowerCase().includes(query.toLowerCase()));
}

const withUser = (username) => {
    cy.fixture("userdata").then((testUser) => {
        const filteredUsers = filterByUsername(testUser, username);
        if (filteredUsers.length > 0) {
            return filteredUsers[0];
        }
        throw `no data found for user ${username}`;
    })
};

const withImportData = (userCount) => {
    cy.fixture("importdata").then(data => {
        const filteredImportData = data.filter(el => el.userCount === userCount);
        if (filteredImportData.length > 0) {
            return filteredImportData[0];
        }
        throw `no data found for userCount ${userCount}`;
    })
};

const createUser = (username, givenName, surname, displayName, mail, password, pwdReset = false, groups = null) => {
    cy.api({
        method: "POST",
        url: Cypress.config().baseUrl + "/usermgt/api/users/",
        followRedirect: false,
        auth: {
            'user': env.GetAdminUsername(),
            'pass': env.GetAdminPassword()
        },
        headers: {
            'Content-Type': 'application/json; charset=UTF-8',
        },
        body: {
            'username': username,
            'givenname': givenName,
            'surname': surname,
            'displayName': displayName,
            'mail': mail,
            'password': password,
            'pwdReset': pwdReset,
            'memberOf': [],
        }
    }).then((response) => {
        expect(response.status).to.eq(201)
        if (groups) {
            for (const groupsKey in groups) {
                let group = groups[groupsKey]
                cy.usermgtTryDeleteGroup(group)
                cy.usermgtCreateGroup(group, "")
                cy.usermgtAddMemberToGroup(group, username)
            }
        }
    })
}

const deleteUser = (username) => {
    cy.api({
        method: "DELETE",
        url: Cypress.config().baseUrl + "/usermgt/api/users/" + username,
        auth: {
            'user': env.GetAdminUsername(),
            'pass': env.GetAdminPassword()
        }
    }).then((response) => {
        expect(response.status).to.eq(204)
    })
}

const tryDeleteUser = (username) => {
    cy.api({
        method: "DELETE",
        url: Cypress.config().baseUrl + "/usermgt/api/users/" + username,
        failOnStatusCode: false,
        auth: {
            'user': env.GetAdminUsername(),
            'pass': env.GetAdminPassword()
        }
    })
}

const cleanupTestUsers = () => {
    cy.api({
        method: "GET",
        url: Cypress.config().baseUrl + "/usermgt/api/users?limit=100000",
        failOnStatusCode: false,
        auth: {
            'user': env.GetAdminUsername(),
            'pass': env.GetAdminPassword()
        }
    }).then(response => {
        expect(response.status).to.eq(200);
        return response.body.entries.filter(el => el.displayName.startsWith("Tester"));
    }).then(testUsers => {
        testUsers.filter(testUser => {
            cy.usermgtDeleteUser(testUser.username);
        })
    });
};

function tryDeleteGroup(groupName) {
    cy.api({
        method: "DELETE",
        url: Cypress.config().baseUrl + "/usermgt/api/groups/" + groupName,
        followRedirect: false,
        failOnStatusCode: false,
        auth: {
            'user': env.GetAdminUsername(),
            'pass': env.GetAdminPassword()
        }
    })
}

const getGroup = (name) => {
    cy.api({
        method: "POST",
        url: Cypress.config().baseUrl + "/usermgt/api/groups/" + name,
        followRedirect: false,
        auth: {
            'user': env.GetAdminUsername(),
            'pass': env.GetAdminPassword()
        }
    }).then((response) => {
        expect(response.status).to.eq(201)
        return response.body
    })
}

const createGroup = (name, description) => {
    cy.api({
        method: "POST",
        url: Cypress.config().baseUrl + "/usermgt/api/groups",
        followRedirect: false,
        auth: {
            'user': env.GetAdminUsername(),
            'pass': env.GetAdminPassword()
        },
        headers: {
            'Content-Type': 'application/json; charset=UTF-8',
        },
        body: {
            'name': name,
            'description': description,
            'members': []
        }
    }).then((response) => {
        expect(response.status).to.eq(201)
    })
}

const deleteGroup = (groupName) => {
    cy.api({
        method: "DELETE",
        url: Cypress.config().baseUrl + "/usermgt/api/groups/" + groupName,
        followRedirect: false,
        auth: {
            'user': env.GetAdminUsername(),
            'pass': env.GetAdminPassword()
        }
    }).then((response) => {
        expect(response.status).to.eq(204)
    })
}

function addMemberToGroup(groupName, username) {
    cy.api({
        method: "POST",
        url: Cypress.config().baseUrl + "/usermgt/api/groups/" + groupName + "/members/" + username,
        auth: {
            'user': env.GetAdminUsername(),
            'pass': env.GetAdminPassword()
        }
    }).then((response) => {
        expect(response.status).to.eq(204)
    })
}

const removeMemberFromGroup = (groupName, username) => {
    cy.api({
        method: "DELETE",
        url: Cypress.config().baseUrl + "/usermgt/api/groups/" + groupName + "/members/" + username,
        auth: {
            'user': env.GetAdminUsername(),
            'pass': env.GetAdminPassword()
        }
    }).then((response) => {
        expect(response.status).to.eq(204)
    })
}

const withClearCookies = (func) => {
    return (...data) => {
        cy.clearCookies();
        func(...data);
    }
}

// common
Cypress.Commands.add("logout", logout)
Cypress.Commands.add("login", login)
Cypress.Commands.add("withUser", withUser)
Cypress.Commands.add("withImportData", withImportData)

// users
Cypress.Commands.add("usermgtCreateUser", withClearCookies(createUser))
Cypress.Commands.add("usermgtDeleteUser", withClearCookies(deleteUser))
Cypress.Commands.add("usermgtTryDeleteUser", withClearCookies(tryDeleteUser))
Cypress.Commands.add("usermgtCleanupTestUsers", withClearCookies(cleanupTestUsers))

// groups
Cypress.Commands.add("usermgtGetGroup", withClearCookies(getGroup))
Cypress.Commands.add("usermgtCreateGroup", withClearCookies(createGroup))
Cypress.Commands.add("usermgtDeleteGroup", withClearCookies(deleteGroup))
Cypress.Commands.add("usermgtTryDeleteGroup", withClearCookies(tryDeleteGroup))
Cypress.Commands.add("usermgtAddMemberToGroup", addMemberToGroup)
Cypress.Commands.add("usermgtRemoveMemberFromGroup", withClearCookies(removeMemberFromGroup))