Feature: Tests for the functionality to prompt a user for a password change at the next login

  Background:
    Given the user "testuser" exists
    And the user is member of the admin user group

  @clean_new_user
  Scenario: a newly created user with the password reset flag enabled logs himself in and must change his password
    When the user opens the users page
    And the user clicks on the create-user button
    And the user fills the user-form for a user with the name "newuser"
    And the user enables the password reset flag
    And the user clicks save
    And the user logs out by visiting the cas logout page
    And the newly created user logs in
    Then the user is asked to change his password

  @clean_before
  Scenario: an already existing user cannot set the password reset flag for himself
    Given the user "testuser" is member of the group "cesManager"
    When the user logs out by visiting the cas logout page
    And the user opens the dogu start page
    And the test user logs in with correct credentials
    And the user opens the users page
    And the user waits a few seconds
    When the user sets the users-filter to "testuser"
    And the user clicks on the edit-user button for the user "testuser"
    Then the password reset flag is not visible

  Scenario: upon opening the user edit form, the password reset flag is set to unchecked
    Given the user opens the users page
    When the user clicks on the create-user button
    Then the password reset flag is unchecked
