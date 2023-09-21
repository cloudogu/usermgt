Feature: Tests for editing new users.

  Background:
    Given the user "manager" exists
    And the user "manager" is member of the group "cesManager"
    And the user "testuser" exists
    When the user opens the users page
    And the user sets the users-filter to "testuser_1"
    And the user clicks on the edit-user button for the user "testuser_1"
    Then the edit-user-page for user "testuser_1" is shown

  Scenario: a user who is manager wants to edit the description of a user
    Given the user edits the user-displayName to "New Displayname"
    And the user clicks save
    When the user sets the users-filter to "testuser_1"
    And the users-page contains the user "testuser_1"
    Then the users-page contains the displayName "New Displayname"

  Scenario: a user who is manager wants to add a group to a user
    Given the user adds the group "testGroup_1" to the user
    And the user clicks save
    And the user sets the users-filter to "testuser_1"
    And the users-page contains the user "testuser_1"
    When the user clicks on the edit-user button for the user "testuser_1"
    Then a group named "testGroup_1" is assigned to the user

  Scenario: a user who is manager wants to remove a group from a user
    Given the user "testuser" is member of the group "testGroup_1"
    When the user removes the group "testGroup_1" from the user
    And the user clicks save
    And the user clicks on the edit-user button for the user "testuser_1"
    Then the user has no groups