Feature: Tests for list of users.

  Background:
    Given the user "manager" exists
    And the user "manager" is member of the group "cesManager"
    And "25" test-users exist


  Scenario: a user enters the user-page
    When the user opens the users page
    Then the users-page is shown

  Scenario: a user who is manager wants see the list of users
    When the user opens the users page
    Then the users-page contains at least "20" users

   @clean_after
   Scenario: a user who is manager selects the second page of users
    When the user opens the users page
    And the user selects the next users-page
    Then the users-page contains at least "5" users
