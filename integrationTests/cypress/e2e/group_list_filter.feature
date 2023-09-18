Feature: Tests for list of groups.

  Background:
    Given the user "manager" exists
    And the user "manager" is member of the group "cesManager"
    And "25" test-groups exist

  Scenario: a user who is manager wants to filter testGroup_2 group
    When the user opens the groups page
    And the user sets the groups-filter to "testGroup_2"
    Then the groups-page contains exactly "7" groups
    
  Scenario: a user who is manager wants to filter forBar_Not_Found group
    When the user opens the groups page
    And the user sets the groups-filter to "forBar_Not_Found"
    Then the groups-page contains exactly "0" groups
    
  Scenario: a user who is manager wants to clear the filter
    When the user opens the groups page
    And the user sets the groups-filter to "testGroup_2"
    And the user clears the groups-filter
    Then the groups-page contains exactly "20" groups