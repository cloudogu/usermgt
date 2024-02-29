Feature: Tests for list of groups.

  Background:
    Given the user "manager" exists
    And the user "manager" is member of the group "cesManager"
    And "32" test-groups exist

  Scenario: a user who is manager wants see the list of groups
    When the user opens the groups page
    Then the groups-page is shown
    And the groups-page contains the group "cesManager"

  Scenario: a user who is manager wants see the first page of groups
    When the user opens the groups page
    Then the groups-page contains exactly "25" groups
    
  Scenario: a user who is manager wants see the second page of groups
    When the user opens the groups page
    And the user selects the next groups-page
    Then the groups-page contains at least "7" groups
