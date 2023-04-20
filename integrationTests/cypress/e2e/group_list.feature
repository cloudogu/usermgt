Feature: Tests for list of groups.

  Scenario: a user who is manager wants see the list of groups
    Given the user "manager" exists
    And the user "manager" is member of the group "cesManager"
    When the user opens the groups page
    Then the groups-page is shown
    And the groups-page contains the group "cesManager"

  Scenario: a user who is manager wants see the all pages of groups
    Given the user "manager" exists
    And the user "manager" is member of the group "cesManager"
    And "25" test-groups exist
    When the user opens the groups page
    Then the groups-page is shown
    When the user selects the "1" groups-page
    Then the groups-page contains exactly "20" groups
    When the user selects the "2" groups-page
    Then the groups-page contains at least "5" groups

  Scenario: a user who is manager wants filter groups
    Given the user "manager" exists
    And the user "manager" is member of the group "cesManager"
    And "25" test-groups exist
    When the user opens the groups page
    Then the groups-page is shown
    When the user sets the groups-filter to "testGroup_2"
    Then the groups-page contains exactly "7" groups
    When the user sets the groups-filter to "forBar_Not_Found"
    Then the groups-page contains exactly "0" groups
    When the user clears the groups-filter
    Then the groups-page contains exactly "20" groups