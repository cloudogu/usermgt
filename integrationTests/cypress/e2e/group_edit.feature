Feature: Tests for editing new groups.

  Background:
    Given the user "manager" exists
    And the user "manager" is member of the group "cesManager"
    And "1" test-groups exist
    And the user opens the groups page
    And the user sets the groups-filter to "testGroup_1"
    And the user clicks on the edit-group button for the group "testGroup_1"
    And the edit-group-page for group "testGroup_1" is shown

  Scenario: a user who is manager wants to edit the description of a group
    Given the user edits the group-description to "Better description"
    And the user submits the group-form
    When the user sets the groups-filter to "testGroup_1"
    Then the groups-page contains the group "testGroup_1"
    And the groups-page contains the group-description "Better description"

  @requires_testuser
  Scenario: a user who is manager wants to add a member to a group
    Given the user adds the member "testuser" to the group
    And the user submits the group-form
    And the user sets the groups-filter to "testGroup_1"
    When the user clicks on the edit-group button for the group "testGroup_1"
    Then a user named "testuser" is member of the group

  @requires_testuser
  Scenario: a user who is manager wants to remove a member from a group
    Given the user "testuser" is member of the group "testGroup_1"
    And the user removes the member "testuser" from the group
    And the user submits the group-form
    When the user clicks on the edit-group button for the group "testGroup_1"
    Then the group has no members