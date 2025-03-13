Feature: Tests for uploading file with wrong data format (txt)

  Background:
    Given the user "manager" exists
    And the user "manager" is member of the group "cesManager"
    And the user "manager" with password "newuserpassword1234A$" is logged in

  Scenario: a user selects a file for upload
    When the user opens the user import page
    And the user selects the file "userimport4.txt"
    Then no content is displayed and upload is not possible
