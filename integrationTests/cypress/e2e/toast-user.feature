Feature: Tests that verify that an appropriate toast is shown when creating, editing or deleting users

  Background:
    Given the user "manager" exists
    And the user "manager" is member of the group "cesManager"
    When the user opens the users page
    And the user clicks on the create-user button
    Then the new-user-page is shown

  Scenario: toast on user creation is shown
    Given the user clicks on the create-user button
    And the new-user-page is shown
    And the user fills the user-form for a user with the name "testUser_new"
    And the user clicks save
    Then a success alert will be shown containing the text "testUser_new"
