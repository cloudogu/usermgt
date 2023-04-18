Feature: Tests for deleting users.

  Scenario: a user who is manager wants to delete a user
    Given the user "manager" exists
    And the user "manager" is member of the group "cesManager"
    And "1" test-users exist
    When the user opens the users page
    And the user sets the users-filter to "testuser_1"
    And the user clicks on the delete-user button for the user "testuser_1"
    And the user confirms the delete-user-confirmation-dialog
    And the user sets the users-filter to "testuser_1"
    Then the users-page contains exactly "0" users