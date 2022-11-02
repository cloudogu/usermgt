Feature: Tests for the verification of the csv import for users.

  @requires_testuser
  Scenario: a user who is manager wants to import five users
    Given the user is logged into the CES
    And the user is member of the cesManager group
    When the user sends the upload request
    Then the import is done successful
    And one user is imported

  @requires_testuser
  Scenario: a user who is not manager wants to import five users
    Given the user is logged into the CES
    When the user sends the upload request
    Then the access to the endpoint is denied