Feature: Tests for the verification of the password policy

  Background:
    Given the user "testuser" exists
    And the user is logged into the CES
    And the user opens his own page in usermgt

  Scenario: a user deletes his password input is shown all password rules
    When the user deletes his password input
    Then the password entry is marked as invalid
    And all password rules are displayed

  Scenario: a user enters a valid password is shown that his confirm entry is not filled out yet
    Given the user deletes his password input
    When the user enters a valid password
    Then the password entry is marked as valid
    And the password-confirm entry is marked as invalid
    And the password-confirm rules are displayed

  Scenario: a user who entered a valid password, enters a valid confirm entry
    Given the user deletes his password input
    When the user enters a valid password
    And the user enters a valid confirm-password
    Then the password entry is marked as valid
    And the password-confirm entry is marked as valid
