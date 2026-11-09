import {After, Before, Then, When} from "@badeball/cypress-cucumber-preprocessor";
import {patElement, readPATCount} from "./pat-helpers";

// Only this scenario's account may be removed by the failure cleanup.
let createdRobotName: string | undefined;

Before({tags: "@created_robot"}, () => {
    createdRobotName = undefined;
});

After({tags: "@created_robot"}, () => {
    if (!createdRobotName) return;
    // Resolve the current ID by name because renewal replaces the PAT and its ID.
    cy.request("GET", "/usermgt/api/robots").then(response => {
        const robots = response.body as {id: string; displayName: string}[];
        robots.filter(robot => robot.displayName === createdRobotName).forEach(robot => {
            cy.request("DELETE", `/usermgt/api/robots/${encodeURIComponent(robot.id)}`)
                .its("status").should("eq", 204);
        });
    });
});

const robotCount = () => patElement("robots-count").should("be.visible");
const robotTable = () => patElement("robot-tokens-table").should("be.visible");
const robotDialog = () => cy.get('[role="dialog"]').should("be.visible");
// SegmentedSelect.Content adds the suffix "-viewport" to its options container's test ID.
const robotExpiryOptions = () => patElement("robots-create-pat-expiry-select-viewport")
    .should("be.visible").find('[role="option"]');

function firstRobotPage(): void {
    patElement("robot-tokens-pagination-back").then(button => {
        if (!button.is(':disabled, [aria-disabled="true"]')) {
            cy.wrap(button).click();
            firstRobotPage();
        }
    });
}

function findRobotRow(name: string): void {
    robotTable().then(table => {
        const row = table.find("tbody tr").filter((_, element) =>
            Cypress.$(element).find("a").text().trim() === name);
        if (row.length) {
            cy.wrap(row).should("have.length", 1).and("be.visible").as("robotRow");
            return;
        }
        patElement("robot-tokens-pagination-forward")
            .should("not.be.disabled")
            .and("not.have.attr", "aria-disabled", "true").click();
        findRobotRow(name);
    });
}

function assertRobotAbsent(name: string): void {
    robotTable().find("tbody").should(body => {
        const matches = body.find("a").filter((_, link) => Cypress.$(link).text().trim() === name);
        expect(matches, "deleted account on this page").to.have.length(0);
    });
    patElement("robot-tokens-pagination-forward").then(button => {
        if (!button.is(':disabled, [aria-disabled="true"]')) {
            cy.wrap(button).click();
            assertRobotAbsent(name);
        }
    });
}

function successDialogToken() {
    cy.get<string>("@robotName").then(name => {
        robotDialog().should("contain.text", name);
    });
    cy.get<string | null>("@robotExpiresAt").then(expiresAt => {
        robotDialog().should("contain.text", expiresAt
            ? new Date(expiresAt).toLocaleDateString("de-DE")
            : "Never");
    });
    return robotDialog().find<HTMLInputElement>("#created-robot-token")
        .should("be.visible").invoke("val")
        .should("be.a", "string").and("not.be.empty");
}

function assertRobotCount(increase: number): void {
    cy.get<number>("@initialRobotCount").then(initialCount => {
        robotCount().should(badge => {
            expect(readPATCount(badge), "technical account count").to.eq(initialCount + increase);
        });
    });
}

When("the user opens the technical accounts page", () => {
    cy.visit("/usermgt/robots");
    cy.clickWarpMenuCheckboxIfPossible();
    robotCount();
});

Then("the user remembers the current technical account count", () => {
    robotCount().should(badge => {
        readPATCount(badge);
    }).then(badge => {
        cy.wrap(readPATCount(badge)).as("initialRobotCount");
    });
});

When("the user opens the technical account creation form", () => {
    patElement("robots-create-robot").click();
    cy.location("pathname").should("eq", "/usermgt/robots/createRobot");
    patElement("security-create-pat-name-input").should("be.visible");
});

When("the user enters a technical account name with the current timestamp", () => {
    const name = `robot-${Date.now()}`;
    createdRobotName = name;
    cy.wrap(name).as("robotName");
    patElement("security-create-pat-name-input").type(name);
});

function saveTechnicalAccount(validityInDays: number): void {
    cy.intercept("POST", "**/usermgt/api/robots").as("createRobot");
    patElement("security-create-pat-submit").click();
    cy.wait("@createRobot").then(({request, response}) => {
        expect(response?.statusCode).to.eq(201);
        cy.wrap(response!.body.expiresAt ?? null).as("robotExpiresAt");
        cy.get<string>("@robotName").then(name => {
            expect(request.body.displayName).to.eq(name);
            expect(response!.body.displayName).to.eq(name);
        });
        expect(request.body.scope).to.eq("/*");
        if (validityInDays === 0) {
            expect(request.body.expiresAt).to.be.undefined;
            expect(response!.body.expiresAt == null, "account never expires").to.eq(true);
        } else {
            assertValidity(request.body.expiresAt, validityInDays);
            expect(Date.parse(response!.body.expiresAt)).to.eq(Date.parse(request.body.expiresAt));
        }
    });
}

function assertValidity(expiresAt: string, days: number): void {
    const remainingTime = Date.parse(expiresAt) - Date.now();
    // Calendar days may cross a daylight-saving boundary; allow that hour and request latency.
    expect(remainingTime, `validity of ${days} days`).to.be.closeTo(days * 86400000, 3660000);
}

When("the user saves the technical account", () => saveTechnicalAccount(0));
When("the user saves the technical account with a validity of {int} days", saveTechnicalAccount);

Then("the technical accounts overview is shown", () => {
    cy.location("pathname").should("eq", "/usermgt/robots");
    // The overview is behind the modal until the secret has been acknowledged.
    patElement("robots-create-robot").should("exist");
    patElement("robots-count").should("exist");
});

Then("the technical account success dialog shows a PAT which is remembered", () => {
    successDialogToken().then(token => cy.wrap(token, {log: false}).as("originalRobotPAT"));
});

When("the user closes the technical account success dialog", () => {
    patElement("robots-created-pat-close").click();
    cy.get('[role="dialog"]').should("not.exist");
});

Then("the new technical account is listed as active without expiration", () => {
    cy.get<string>("@robotName").then(name => {
        firstRobotPage();
        findRobotRow(name);
        cy.get("@robotRow").find("td").eq(1).should("contain.text", "Active");
        cy.get("@robotRow").find("td").eq(3).should("have.text", "Never");
    });
});

Then("the technical account count has increased by {int}", assertRobotCount);
Then("the technical account count matches the remembered count", () => assertRobotCount(0));

When("the user clicks the new technical account's renew action", () => {
    cy.intercept("PUT", "**/usermgt/api/robots/*").as("renewRobot");
    cy.get<string>("@robotName").then(name => {
        firstRobotPage();
        findRobotRow(name);
        // The table exposes Renew followed by Delete as its two icon actions.
        cy.get("@robotRow").find("button").should("have.length", 2).first().click();
    });
    cy.wait("@renewRobot").its("response.statusCode").should("eq", 201);
});

Then("the technical account success dialog shows a different PAT", () => {
    successDialogToken().then(token => {
        cy.get<string>("@originalRobotPAT", {log: false}).then(original => {
            expect(token !== original, "renewal returns a new PAT").to.eq(true);
        });
    });
});

When("the user clicks the new technical account's delete action", () => {
    cy.get<string>("@robotName").then(name => {
        firstRobotPage();
        findRobotRow(name);
        cy.get("@robotRow").find("button").should("have.length", 2).last().click();
    });
});

Then("the technical account deletion confirmation dialog is shown", () => {
    cy.get<string>("@robotName").then(name => {
        robotDialog().should("contain.text", name);
        robotDialog().contains("button", /^Delete$/).should("be.visible");
        robotDialog().contains("button", /^Cancel$/).should("be.visible");
    });
});

When("the user confirms the technical account deletion", () => {
    cy.intercept("DELETE", "**/usermgt/api/robots/*").as("deleteRobot");
    robotDialog().contains("button", /^Delete$/).click();
    cy.wait("@deleteRobot").its("response.statusCode").should("eq", 204);
    cy.get('[role="dialog"]').should("not.exist").then(() => {
        createdRobotName = undefined;
    });
});

Then("the new technical account is no longer listed", () => {
    cy.get<string>("@robotName").then(name => {
        firstRobotPage();
        assertRobotAbsent(name);
    });
});

When("the user opens the new technical account's details by clicking its name", () => {
    cy.get<string>("@robotName").then(name => {
        firstRobotPage();
        findRobotRow(name);
        cy.get("@robotRow").find("td").eq(3).invoke("text").then(date => {
            cy.wrap(date.trim()).as("robotDisplayedExpiration");
        });
        cy.get("@robotRow").find("a").should("have.text", name).click();
    });
});

Then("the technical account detail page is shown", () => {
    cy.location("pathname").should("match", /^\/usermgt\/robots\/pats\/[^/]+$/);
    cy.get<string>("@robotName").then(name => {
        cy.get("h2").should("be.visible").and("have.text", name);
    });
    patElement("robots-create-pat-expiry-select-trigger").should("be.visible");
});

Then("the technical account renewal time selection contains the current expiration date", () => {
    cy.get<string>("@robotDisplayedExpiration").then(date => {
        expect(date, "current account expiration").not.to.eq("Never");
        expect(date).not.to.be.empty;
        patElement("robots-create-pat-expiry-select-trigger").click();
        robotExpiryOptions()
            .filter((_, option) => Cypress.$(option).text().trim() === date)
            .should("have.length", 1).and("be.visible");
    });
});

When("the user selects {int} days and renews the technical account from its details", (days: number) => {
    // The current-date option shares a test ID with the seven-day option; use its visible label.
    robotExpiryOptions().contains(new RegExp(`^${days} days$`)).click();
    cy.intercept("PUT", "**/usermgt/api/robots/*").as("renewRobotFromDetails");
    cy.get('form button[type="submit"]').should("be.visible").click();
    cy.wait("@renewRobotFromDetails").then(({request, response}) => {
        expect(response?.statusCode).to.eq(201);
        assertValidity(request.body.expiredAt, days);
        expect(Date.parse(response!.body.expiresAt)).to.eq(Date.parse(request.body.expiredAt));
        cy.wrap(response!.body.expiresAt).as("robotExpiresAt");
    });
});

When("the user creates a technical account via the API that expires in {int} seconds", (seconds: number) => {
    // Calculate the short lifetime when the request runs, after login and navigation have finished.
    cy.then(() => {
        const name = `robot-${Date.now()}`;
        const expiresAt = new Date(Date.now() + seconds * 1000).toISOString();
        createdRobotName = name;
        cy.wrap(name).as("robotName");
        cy.request({
            method: "POST",
            url: "/usermgt/api/robots",
            body: {displayName: name, scope: "/*", expiresAt},
            log: false,
        }).then(response => {
            expect(response.status).to.eq(201);
            expect(response.body.displayName).to.eq(name);
            expect(Date.parse(response.body.expiresAt)).to.eq(Date.parse(expiresAt));
            cy.wrap(response.body.expiresAt).as("robotExpiresAt");
        });
    });
});

When("the user waits {int} seconds and reloads the technical accounts overview", (seconds: number) => {
    // Use real time: the expiration also has to pass on the server.
    cy.wait(seconds * 1000);
    cy.intercept("GET", "**/usermgt/api/robots").as("reloadedRobots");
    cy.reload();
    cy.wait("@reloadedRobots").its("response.statusCode").should("eq", 200);
    robotCount();
});

Then("the new technical account is listed as expired", () => {
    cy.get<string>("@robotName").then(name => {
        firstRobotPage();
        findRobotRow(name);
        cy.get("@robotRow").find("td").eq(1).should("be.visible").and("contain.text", "Expired");
    });
});

Then("the technical account renewal time selection contains only the standard periods", () => {
    patElement("robots-create-pat-expiry-select-trigger").click();
    robotExpiryOptions()
        .should("have.length", 5).should(options => {
            const labels = options.toArray().map(option => Cypress.$(option).text().trim());
            expect(labels, "only the standard renewal periods, without the expired date")
                .to.deep.eq(["7 days", "30 days", "60 days", "90 days", "Never"]);
        });
});
