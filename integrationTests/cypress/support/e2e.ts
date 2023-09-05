// Loads all commands from the dogu integration library into this project
import '@bahmutov/cy-api'
import doguTestLibrary from "@cloudogu/dogu-integration-test-library";
import env from "@cloudogu/dogu-integration-test-library/lib/environment_variables";

doguTestLibrary.registerCommands();

declare global {
    namespace Cypress {
        interface Chainable {
            clickWarpMenuCheckboxIfPossible(): void
            login(username: string, password: string, retryCount: number|undefined): void
            usermgtCreateGroup(groupname: string, description: string): void
            usermgtTryDeleteGroup(groupname: string): void
            usermgtAddMemberToGroup(groupname: string, username: string): void
            usermgtRemoveMemberFromGroup(groupname: string, username: string): void
            usermgtCreateUser(username: string, givenname: string, suname: string, displayname: string, mail: string, password: string, pwdReset: boolean|undefined, groups: any|undefined): void
            usermgtDeleteUser(username: string): void
            usermgtTryDeleteUser(username: string): void
            usermgtCleanupTestUsers(): void
            usermgtGetGroup(groupname: string): void
            usermgtDeleteGroup(groupname: string): void
            usermgtCleanupTestGroups(): void
            withUser(username: string): any
            logout(): void
            deleteUserFromDoguViaAPI(usernamen: string, exitOnFail: boolean)
        }
    }
}


/**
 * Overwrite logut and login behavior as the session cookie for the usermgt api-requests are not
 * deleted correctly. As such we need to delete them manually as otherwise the user is falsy logged
 * in and not redirected to `/cas/login`.
 */
const logout = (): void => {
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
    }).then((response) => {
        expect(response.status).to.eq(200);
        // @ts-ignore
        return response.body.entries.filter(el => el.displayName.startsWith("Tester") || el.username.startsWith("testUser"));
    }).then(testUsers => {
        testUsers.filter(testUser => {
            cy.usermgtDeleteUser(testUser.username);
        })
    });
};

const cleanupTestGroups = () => {
    cy.api({
        method: "GET",
        url: Cypress.config().baseUrl + "/usermgt/api/groups?limit=100000",
        failOnStatusCode: false,
        auth: {
            'user': env.GetAdminUsername(),
            'pass': env.GetAdminPassword()
        }
    }).then((response) => {
        expect(response.status).to.eq(200);
        // @ts-ignore
        return response.body.entries.filter(el => el.name.startsWith("testGroup"));
    }).then(testGroups => {
        testGroups.filter(testGroup => {
            cy.usermgtDeleteGroup(testGroup.name);
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

/**
 * Deletes a user from the dogu via an API call.
 * @param {String} username - The username of the user.
 * @param {boolean} exitOnFail - Determines whether the test should fail when the request did not succeed. Default: false
 */
const deleteUserFromDoguViaAPI = (username, exitOnFail = false) => {
    // do nothing
}

// common
Cypress.Commands.add("logout", logout)
Cypress.Commands.add("login", login)
Cypress.Commands.add("withUser", withUser)
Cypress.Commands.add("deleteUserFromDoguViaAPI", deleteUserFromDoguViaAPI)

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
Cypress.Commands.add("usermgtCleanupTestGroups", withClearCookies(cleanupTestGroups))