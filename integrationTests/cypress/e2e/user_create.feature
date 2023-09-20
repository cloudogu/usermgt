Feature: Tests for creating new users.

  Background:
    Given the user "manager" exists
    And the user "manager" is member of the group "cesManager"
    When the user opens the users page
    And the user clicks on the create-user button
    Then the new-user-page is shown

  Scenario: a user who is manager wants to create a new user
    When the user fills the user-form for a user with the name "testUser_new"
    And the user clicks save
    When the user sets the users-filter to "testUser_new"
    Then the users-page contains the user "testUser_new"

  Scenario: a user who is manager cannot to create a new user without a valid name
    When the user fills the user-form for a user with the name "a"
    Then the username-field is marked as invalid

  Scenario: a user who is manager cannot to create a new user with an existing username or e-mail
    When the user fills the user-form for a user with the name "testUser_new"
    And the user clicks save
    And the user sets the users-filter to "testUser_new"
    And the users-page contains the user "testUser_new"
    And the user clicks on the create-user button
    And the new-user-page is shown
    When the user fills the user-form for a user with the name "testUser_new"
    And the user clicks save
    Then an user-exists-error is shown an the fields are marked invalid

  Scenario: a user who is manager wants to create a new user with assigned groups
    When the user fills the user-form for a user with the name "testUser_new"
    And the user adds the group "testGroup_1" to the user
    And the user clicks save
    When the user sets the users-filter to "testUser_new"
    And  the users-page contains the user "testUser_new"
    And the user clicks on the edit-user button for the user "testUser_new"
    Then a group named "testGroup_1" is assigned to the user

