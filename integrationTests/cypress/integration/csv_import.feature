Feature: Tests for the verification of the csv import for users.


# Integration Test not doable due to redirection to Login Side and thus always returning 200, unable to find reason
#  Scenario: a user who is manager wants to import a user
#    Given the user is member of the cesManager group
#    When the user "bob" sends the upload request
#    Then the user "Tester1" exists
#
#  Scenario: a user who is not manager wants to import a user
#    When the user "gary" sends the upload request, but is not allowed to
#    Then the user "Tester1" does not exists