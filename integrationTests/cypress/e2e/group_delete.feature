Feature: Tests for deleting groups.

  Scenario: a user who is manager wants to delete a group
    Given the user "manager" exists
    And the user "manager" is member of the group "cesManager"
    And the user "testUser" exists
    And "1" test-groups exist
    When the user opens the groups page
    And the user sets the groups-filter to "testGroup_1"
    And the user clicks on the delete-group button for the group "testGroup_1"
    And the user confirms the delete-group-confirmation-dialog
    Then a success alert will be shown containing the text "testGroup_1"
    When the user sets the groups-filter to "testGroup_1"
    Then the groups-page contains exactly "0" groups