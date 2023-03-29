Feature: Tests for the verification of the password policy

  @requires_testuser
  Scenario: a user who wants to change his password is shown the password rules
    Given the user is logged into the CES
    When the user opens his own page in usermgt
    And the user deletes his password input
    Then the password entry is marked as invalid
    And all password rules are displayed
    And all password rules are marked as not fullfilled
    When the user enters a valid password
    Then the password entry is marked as valid
    And all password rules are displayed
    And all password rules are marked as fullfilled