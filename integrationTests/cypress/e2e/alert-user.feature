Feature: Tests that verify that an appropriate toast is shown when creating, editing or deleting users

  Background:
    Given the user "manager" exists
    And the user "manager" is member of the group "cesManager"
    When the user opens the users page
    And the user clicks on the create-user button
    Then the new-user-page is shown

  @clean_new_user
  Scenario: toast on user creation is shown
    When the user fills the user-form for a user with the name "testuser_new"
    And the user clicks save
    Then a success alert will be shown containing the text "User 'testuser_new' was saved successfully."

  @reduce_user_env
  Scenario: toast on user deletion is shown
    Given "1" test-users exist
    When the user opens the users page
    And the user sets the users-filter to "testuser_1"
    And the user clicks on the delete-user button for the user "testuser_1"
    And the user confirms the delete-user-confirmation-dialog
    Then a success alert will be shown containing the text "The user 'testuser_1' was deleted successfully."
