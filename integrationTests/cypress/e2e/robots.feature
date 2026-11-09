@created_robot
Feature: Manage technical accounts

  Background:
    Given the user "manager" exists
    And the user "manager" is member of the group "cesManager"
    And the user "manager" with password "newuserpassword1234A$" is logged in

  Scenario: Create, renew and delete a technical account from the overview
    When the user opens the technical accounts page
    Then the user remembers the current technical account count
    When the user opens the technical account creation form
    And the user enters a technical account name with the current timestamp
    And the user selects the PAT scope "Alle Dogus"
    And the user selects the PAT expiry option "0"
    And the user saves the technical account
    Then the technical accounts overview is shown
    And the technical account success dialog shows a PAT which is remembered
    When the user closes the technical account success dialog
    Then the new technical account is listed as active without expiration
    And the technical account count has increased by 1
    When the user clicks the new technical account's renew action
    Then the technical account success dialog shows a different PAT
    When the user closes the technical account success dialog
    Then the new technical account is listed as active without expiration
    And the technical account count has increased by 1
    When the user clicks the new technical account's delete action
    Then the technical account deletion confirmation dialog is shown
    When the user confirms the technical account deletion
    Then the new technical account is no longer listed
    And the technical account count matches the remembered count

  Scenario: Renew a technical account with seven days validity from its details
    When the user opens the technical accounts page
    Then the user remembers the current technical account count
    When the user opens the technical account creation form
    And the user enters a technical account name with the current timestamp
    And the user selects the PAT scope "Alle Dogus"
    And the user selects the PAT expiry option "7"
    And the user saves the technical account with a validity of 7 days
    Then the technical accounts overview is shown
    And the technical account success dialog shows a PAT which is remembered
    When the user closes the technical account success dialog
    And the user opens the new technical account's details by clicking its name
    Then the technical account detail page is shown
    And the technical account renewal time selection contains the current expiration date
    When the user selects 7 days and renews the technical account from its details
    Then the technical account success dialog shows a different PAT
    When the user closes the technical account success dialog
    Then the technical account detail page is shown
    When the user opens the technical accounts page
    Then the technical account count has increased by 1
    When the user clicks the new technical account's delete action
    Then the technical account deletion confirmation dialog is shown
    When the user confirms the technical account deletion
    Then the new technical account is no longer listed
    And the technical account count matches the remembered count

  Scenario: Show an expired technical account and only standard renewal periods
    When the user opens the technical accounts page
    And the user creates a technical account via the API that expires in 5 seconds
    And the user waits 10 seconds and reloads the technical accounts overview
    Then the new technical account is listed as expired
    When the user opens the new technical account's details by clicking its name
    Then the technical account detail page is shown
    And the technical account renewal time selection contains only the standard periods
