Feature: Tests for the verification of the csv import for users.

  Scenario: a user who is manager wants to import a user
    Given the user "manager" exists
    And the user "manager" is member of the group "cesManager"
    When the user "manager" sends the upload request with 1 users
    Then the import finished with status code 200
    And the user "Tester1" was created

  Scenario: a user who is not manager wants to import a user
    Given the user "nomanager" exists
    When the user "nomanager" sends the upload request with 1 users
    Then the import finished with status code 401
    And the user "Tester2" does not exists

  Scenario: a user imports five users
    Given the user "manager" exists
    And the user "manager" is member of the group "cesManager"
    When the user "manager" sends the upload request with 5 users
    Then the import finished with status code 200
    And the user "Tester1" was created
    And the user "Tester2" was created
    And the user "Tester3" was created
    And the user "Tester4" was created
    And the user "Tester5" was created

  Scenario: a user imports three users with one malformed user
    Given the user "manager" exists
    And the user "manager" is member of the group "cesManager"
    When the user "manager" sends the upload request with 3 users
    Then the import finished with status code 200
    And the user "Tester1" was created
    And the user "Tester2" does not exists
    And the user "Tester3" was created