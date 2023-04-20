Feature: Tests for list of users.

  Scenario: a user who is manager wants see the list of users
    Given the user "manager" exists
    And the user "manager" is member of the group "cesManager"
    When the user opens the users page
    Then the users-page is shown
    And the users-page contains the user "manager"

  Scenario: a user who is manager wants see the all pages of users
    Given the user "manager" exists
    And the user "manager" is member of the group "cesManager"
    And "25" test-users exist
    When the user opens the users page
    Then the users-page is shown
    When the user selects the "1" users-page
    Then the users-page contains at least "20" users
    When the user selects the "2" users-page
    Then the users-page contains at least "5" users

  Scenario: a user who is manager wants filter users
    Given the user "manager" exists
    And the user "manager" is member of the group "cesManager"
    And "25" test-users exist
    When the user opens the users page
    Then the users-page is shown
    When the user sets the users-filter to "test_2"
    Then the users-page contains exactly "7" users
    When the user sets the users-filter to "forBar_Not_Found"
    Then the users-page contains exactly "0" users
    When the user clears the users-filter
    Then the users-page contains exactly "20" users