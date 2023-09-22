Feature: Tests for creating new groups.

  Background:
    Given the user "manager" exists
    And the user "manager" is member of the group "cesManager"
    When the user opens the groups page
    And the user clicks on the create-group button
    Then the new-group-page is shown

  Scenario: a user who is manager cannot to create a new group without a valid name
    When the user fills the group-form for a group with the name "a"
    Then the group-name-field is marked as invalid

  @clean_new_group
  Scenario: a user who is manager wants to create a new group with members
    Given the user "testuser" exists
    When the user fills the group-form for a group with the name "testGroup_new"
    And the user adds the member "testuser" to the group
    And the user submits the group-form
    And the user sets the groups-filter to "testGroup_new"
    Then the groups-page contains the group "testGroup_new"
    And the user clicks on the edit-group button for the group "testGroup_new"
    And a user named "testuser" is member of the group