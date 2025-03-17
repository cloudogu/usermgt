Feature: Tests for the view restriction on some pages a normal user without manager right cannot access.

  Background:
    Given the user "nomanager" exists
    And the user "nomanager" with password "newuserpassword1234A$" is logged in

  Scenario: a user cannot see the users page contents
    When the user opens the users page
    Then an access denied message will be shown

  Scenario: a user cannot see the groups page contents
    When the user opens the groups page
    Then an access denied message will be shown

  Scenario: a user cannot see the user import page contents
    When the user opens the user import page
    Then an access denied message will be shown

  Scenario: a user cannot see the user import summaries page contents
    When the user opens the user import summaries page
    Then an access denied message will be shown

  Scenario: a user cannot see the user import summary details page contents
    When the user opens the user import summary details page
    Then an access denied message will be shown

  Scenario: a user cannot see the tabs users, groups, user import and import overviews in the navbar
    Then users, groups, user import and import overview should not be visible in the navbar

  Scenario: a user sees the account page after login
    Then the account page for user "nomanager" is shown
