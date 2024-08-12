Feature: Tests for the view restriction on some pages with manager rights.

  Background:
    Given the user "manager" exists
    And the user "manager" is member of the group "cesManager"
    And the user "manager" with password "newuserpassword1234A$" is logged in

  Scenario: a user sees the account page after login
    Then the account page for user "manager" is shown

  Scenario: a user can see the tabs user, groups, user import and import overviews in the navbar
    Then users, groups, user import and import overview should be visible in the navbar

  Scenario: a user can navigate to the user import page
    When the user opens the user import page
    Then the user import page is shown
