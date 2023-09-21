Feature: Tests for editing new users.

  Background:
    Given the user "manager" exists
    And the user "manager" is member of the group "cesManager"
    And the user "testuser" exists
    When the user opens the users page
    And the user sets the users-filter to "testuser"

  Scenario: a user who is manager wants to edit the description of a user
    Given the user clicks on the edit-user button for the user "testuser"
    And the edit-user-page for user "testuser" is shown
    And the user edits the user-displayName to "New Displayname"
    When the user clicks save
    And the user sets the users-filter to "testuser"
    And the users-page contains the user "testuser"
    Then the users-page contains the displayName "New Displayname"

  Scenario: a user who is manager wants to add a group to a user
    Given "1" test-groups exist
    And the user clicks on the edit-user button for the user "testuser"
    And the edit-user-page for user "testuser" is shown
    When the user adds the group "testGroup_1" to the user
    And the user clicks save
    And the user sets the users-filter to "testuser"
    And the users-page contains the user "testuser"
    And the user clicks on the edit-user button for the user "testuser"
    Then a group named "testGroup_1" is assigned to the user

  Scenario: a user who is manager wants to remove a group from a user
    Given "1" test-groups exist
    And the user "testuser" is member of the group "testGroup_1"
    And the user clicks on the edit-user button for the user "testuser"
    And the edit-user-page for user "testuser" is shown
    When the user removes the group "testGroup_1" from the user
    And the user clicks save
    And the user clicks on the edit-user button for the user "testuser"
    Then the user has no groups