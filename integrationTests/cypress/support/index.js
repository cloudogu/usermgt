// Loads all commands from the dogu integration library into this project
const doguTestLibrary = require('@cloudogu/dogu-integration-test-library')
const env = require('@cloudogu/dogu-integration-test-library/lib/environment_variables')
const managerGroup = "cesManager"

doguTestLibrary.registerCommands()

import "./commands/required_commands_for_dogu_lib"

/**
 * Overwrite logut and login behavior as the session cookie for the usermgt api-requests are not
 * deleted correctly. As such we need to delete them manually as otherwise the user is falsy logged 
 * in and not redirected to `/cas/login`.
 */
const logout = () => {
    cy.visit("/cas/logout")
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

/**
 * Returns whether the given user has manager privileges in the CES.
 * @param {String} username - The username of the user to check.
 */
const isCesManager = (username) => {
    cy.usermgtGetUser(username).then(function (response) {
        for (var element of response.memberOf) {
            if (element === managerGroup) {
                return true
            }
        }
        return false
    })
}

/**
 * Promotes an account to a ces manager account. If the given account is already manager it does nothing.
 * @param {String} username - The username of the user to promote.
 */
const promoteAccountToManager = (username) => {
    cy.isCesManager(username).then(function (isAdmin) {
        if (!isAdmin) {
            cy.usermgtAddMemberToGroup(managerGroup, username)
        }
    })
}

Cypress.Commands.add("logout", logout)
Cypress.Commands.add("login", login)
Cypress.Commands.add("isCesManager", isCesManager)
Cypress.Commands.add("promoteAccountToManager", promoteAccountToManager)