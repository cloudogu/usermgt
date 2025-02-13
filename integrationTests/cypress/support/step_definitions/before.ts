import {Before} from "@badeball/cypress-cucumber-preprocessor";
import "cypress-fs";

Before({tags: "@clean_new_group"}, () => {
    cy.withUser("testuser").then(userData => {
        cy.log(userData)
        cy.usermgtTryDeleteGroup("testgroup_new");
    });
})

Before({tags: "@clean_new_user"}, () => {
    cy.withUser("testuser").then(userData => {
        cy.log(userData)
        cy.usermgtTryDeleteUser("testuser_new");
        cy.usermgtTryDeleteUser("newuser");
    });
})
Before({tags: "@clean_before"}, () => {
    cy.log("clean TestUsers");
    cy.usermgtCleanupTestUsers();
    cy.log("clean TestGroups");
    cy.usermgtCleanupTestGroups();
})

Before({tags: "@clear_downloadDir"}, () => {
    const downloadsFolder = Cypress.config("downloadsFolder");
    cy.fsDirExists(downloadsFolder).then((exists: boolean) => {
        if (exists) {
            cy.fsReadDir(downloadsFolder).then((files) => {
                for (const file of files) {
                    console.log("clear file: " + downloadsFolder + file);
                    cy.fsDeleteFile(`${downloadsFolder}/${file}`);
                }
            });
        }
    });
})

Before({tags: "@add_user"}, () => {
    cy.log("add Test user Max Mustermann");
    cy.withUser("mmustermann").then(userData => {
        cy.usermgtCreateUser(userData.username, userData.givenname, userData.surname, userData.displayName, userData.mail, userData.password,false, [])
    })
})

Before({tags: "@clear_mails"}, () => {
    cy.log("delete previous mails in Mailhog");
    cy.mhDeleteAll()
})
