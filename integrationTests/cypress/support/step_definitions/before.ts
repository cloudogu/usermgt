import {Before} from "@badeball/cypress-cucumber-preprocessor";

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
