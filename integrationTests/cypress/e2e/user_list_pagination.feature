Feature: Tests for list of users.

  Background:
    Given the user "manager" exists
    And the user "manager" is member of the group "cesManager"
    And "25" test-users exist

  Scenario: a users enters the user-page
    When the user opens the users page
    Then the users-page is shown

  Scenario: a user who is manager wants see the list of users
    When the user opens the users page
    Then the users-page contains the user "manager"

  Scenario: a user who is manager selects the first page of users
    When the user opens the users page
    And the user selects the "1" users-page
    Then the users-page contains at least "20" users
   
   Scenario: a user who is manager selects the second page of users
    When the user opens the users page
    And the user selects the "2" users-page
    Then the users-page contains at least "5" users