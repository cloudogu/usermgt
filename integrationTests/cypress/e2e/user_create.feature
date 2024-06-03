Feature: Tests for creating new users.

  Background:
    Given the user "manager" exists
    And the user "manager" is member of the group "cesManager"
    When the user opens the users page
    And the user clicks on the create-user button
    Then the new-user-page is shown

  @clean_new_user
  Scenario: a user who is manager wants to create a new user
    When the user fills the user-form for a user with the name "testuser_new"
    And the user clicks save
    And a success alert will be shown containing the text "testuser_new"
    When the user sets the users-filter to "testuser_new"
    Then the users-page contains the user "testuser_new"

  Scenario: a user who is manager cannot to create a new user without a valid name
    When the user fills the user-form for a user with the name "a"
    And the user clicks save
    Then the username-field is marked as invalid

  @clean_new_user
  Scenario: a user who is manager cannot to create a new user with an existing username or e-mail
    When the user fills the user-form for a user with the name "testuser_new"
    And the user clicks save
    And the user sets the users-filter to "testuser_new"
    And the users-page contains the user "testuser_new"
    And the user clicks on the create-user button
    And the new-user-page is shown
    When the user fills the user-form for a user with the name "testuser_new"
    And the user clicks save
    Then an user-exists-error is shown an the fields are marked invalid

  @clean_new_user
  Scenario: a user who is manager wants to create a new user with assigned groups
    Given "1" test-groups exist
    And the user fills the user-form for a user with the name "testuser_new"
    And the user adds the group "testGroup_1" to the user
    And the user clicks save
    When the user sets the users-filter to "testuser_new"
    And the users-page contains the user "testuser_new"
    And the user clicks on the edit-user button for the user "testuser_new"
    Then a group named "testGroup_1" is assigned to the user

