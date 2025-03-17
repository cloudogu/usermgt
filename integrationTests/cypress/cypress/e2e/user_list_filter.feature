Feature: Tests for list of users.

  Background:
    Given the user "manager" exists
    And the user "manager" is member of the group "cesManager"
    And "25" test-users exist

  Scenario: a user who is manager wants to filter test_2 user
    When the user opens the users page
    And the user sets the users-filter to "test_2"
    Then the users-page contains exactly "7" users

  Scenario: a user who is manager wants to filter forBar_Not_Found user
    When the user opens the users page
    And the user sets the users-filter to "forBar_Not_Found"
    Then the users-page contains exactly "0" users
  
  Scenario: a user who is manager wants to clear the filter
    When the user opens the users page
    And the user sets the users-filter to "test_2"
    And the user clears the users-filter
    Then the users-page contains exactly "25" users
