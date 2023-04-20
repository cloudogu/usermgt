Feature: Tests for creating new groups.

  Scenario: a user who is manager wants to create a new group
    Given the user "manager" exists
    And the user "manager" is member of the group "cesManager"
    When the user opens the groups page
    And the user clicks on the create-group button
    Then the new-group-page is shown
    When the user fills the group-form for a group with the name "testGroup_new"
    And the user submits the group-form
    Then a success alert will be shown containing the text "testGroup_new"
    When the user sets the groups-filter to "testGroup_new"
    Then the groups-page contains the group "testGroup_new"

  Scenario: a user who is manager cannot to create a new group without a valid name
    Given the user "manager" exists
    And the user "manager" is member of the group "cesManager"
    When the user opens the groups page
    And the user clicks on the create-group button
    Then the new-group-page is shown
    When the user fills the group-form for a group with the name "a"
    Then the group-name-field is marked as invalid

  Scenario: a user who is manager wants to create a new group with members
    Given the user "manager" exists
    And the user "manager" is member of the group "cesManager"
    And the user "testUser" exists
    When the user opens the groups page
    And the user clicks on the create-group button
    Then the new-group-page is shown
    When the user fills the group-form for a group with the name "testGroup_new"
    And the user adds the member "testuser" to the group
    And the user submits the group-form
    Then a success alert will be shown containing the text "testGroup_new"
    When the user sets the groups-filter to "testGroup_new"
    Then the groups-page contains the group "testGroup_new"
    When the user clicks on the edit-group button for the group "testGroup_new"
    Then a user named "testuser" is member of the group