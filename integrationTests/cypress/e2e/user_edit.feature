Feature: Tests for editing new users.

  Scenario: a user who is manager wants to edit the description of a user
    Given the user "manager" exists
    And the user "manager" is member of the group "cesManager"
    And "1" test-users exist
    When the user opens the users page
    And the user sets the users-filter to "testuser_1"
    And the user clicks on the edit-user button for the user "testuser_1"
    Then the edit-user-page for user "testuser_1" is shown
    When the user edits the user-displayName to "New Displayname"
    And the user clicks save
    And the user sets the users-filter to "testuser_1"
    Then the users-page contains the user "testuser_1"
    Then the users-page contains the displayName "New Displayname"

  Scenario: a user who is manager wants to add a group to a user
    Given the user "manager" exists
    And the user "manager" is member of the group "cesManager"
    And "1" test-groups exist
    And "1" test-users exist
    When the user opens the users page
    And the user sets the users-filter to "testuser_1"
    And the user clicks on the edit-user button for the user "testuser_1"
    Then the edit-user-page for user "testuser_1" is shown
    And the user adds the group "testGroup_1" to the user
    And the user clicks save
    And the user sets the users-filter to "testuser_1"
    Then the users-page contains the user "testuser_1"
    When the user clicks on the edit-user button for the user "testuser_1"
    Then a group named "testGroup_1" is assigned to the user

  Scenario: a user who is manager wants to remove a member from a user
    Given the user "manager" exists
    And the user "manager" is member of the group "cesManager"
    And "1" test-groups exist
    And "1" test-users exist
    When the user opens the users page
    And the user sets the users-filter to "testuser_1"
    And the user clicks on the edit-user button for the user "testuser_1"
    Then the edit-user-page for user "testuser_1" is shown
    When the user adds the group "testGroup_1" to the user
    And the user clicks save
    And the user sets the users-filter to "testuser_1"
    Then the users-page contains the user "testuser_1"
    When the user clicks on the edit-user button for the user "testuser_1"
    Then a group named "testGroup_1" is assigned to the user
    When the user removes the group "testGroup_1" from the user
    And the user clicks save
    And the user sets the users-filter to "testuser_1"
    Then the users-page contains the user "testuser_1"
    When the user clicks on the edit-user button for the user "testuser_1"
    Then the user has no groups