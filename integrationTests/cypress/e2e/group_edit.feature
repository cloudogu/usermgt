Feature: Tests for editing new groups.

  Scenario: a user who is manager wants to edit the description of a group
    Given the user "manager" exists
    And the user "manager" is member of the group "cesManager"
    And "1" test-groups exist
    When the user opens the groups page
    And the user sets the groups-filter to "testGroup_1"
    And the user clicks on the edit-group button for the group "testGroup_1"
    Then the edit-group-page for group "testGroup_1" is shown
    When the user edits the group-description to "Better description"
    And the user submits the group-form
    And the user sets the groups-filter to "testGroup_1"
    Then the groups-page contains the group "testGroup_1"
    Then the groups-page contains the group-description "Better description"

  Scenario: a user who is manager wants to add a member to a group
    Given the user "manager" exists
    And the user "manager" is member of the group "cesManager"
    And the user "testUser" exists
    And "1" test-groups exist
    When the user opens the groups page
    And the user sets the groups-filter to "testGroup_1"
    And the user clicks on the edit-group button for the group "testGroup_1"
    Then the edit-group-page for group "testGroup_1" is shown
    When the user adds the member "testuser" to the group
    And the user submits the group-form
    And the user sets the groups-filter to "testGroup_1"
    Then the groups-page contains the group "testGroup_1"
    When the user clicks on the edit-group button for the group "testGroup_1"
    Then a user named "testuser" is member of the group

  Scenario: a user who is manager wants to remove a member from a group
    Given the user "manager" exists
    And the user "manager" is member of the group "cesManager"
    And the user "testUser" exists
    And "1" test-groups exist
    When the user opens the groups page
    And the user sets the groups-filter to "testGroup_1"
    And the user clicks on the edit-group button for the group "testGroup_1"
    Then the edit-group-page for group "testGroup_1" is shown
    When the user adds the member "testuser" to the group
    And the user submits the group-form
    And the user sets the groups-filter to "testGroup_1"
    Then the groups-page contains the group "testGroup_1"
    When the user clicks on the edit-group button for the group "testGroup_1"
    Then a user named "testuser" is member of the group
    When the user removes the member "testuser" from the group
    And the user submits the group-form
    And the user sets the groups-filter to "testGroup_1"
    Then the groups-page contains the group "testGroup_1"
    When the user clicks on the edit-group button for the group "testGroup_1"
    Then the group has no members