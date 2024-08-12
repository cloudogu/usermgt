Feature: Tests for uploading file with user name that contains a special character

  Background:
    Given the user "manager" exists
    And the user "manager" is member of the group "cesManager"
    And the user "manager" with password "newuserpassword1234A$" is logged in

  Scenario: a user selects a file for upload
    When the user opens the user import page
    And the user selects the file "userimport_Sonderzeichen.csv"
    Then a table of the file content for the user "mm!uster" is displayed

  Scenario: a user uploads a file
    When the user opens the user import page
    And the user uploads the file "userimport_Sonderzeichen.csv"
    Then the user import page shows a failed import

  Scenario: after uploading a file a user inspects the user import details page
    When the user opens the user import details page
    And the user clicks on the line 'Skipped data rows'
    Then the table shows that the username was not in the correct format

  Scenario: after uploading a file a user downloads the import overview
    When the user opens the user import details page
    And the user downloads the import overview
    Then the import result is downloaded and contains error information regarding the file "userimport_Sonderzeichen.csv"

  Scenario: after uploading a file a user inspects the users page
    When the user opens the users page
    Then the new user "mm!uster" was not added

  @clean_user_import
  Scenario: after uploading a file a user inspects the user import summaries page
    When the user opens the user import summaries page
    Then a table with the import information regarding the file "userimport_Sonderzeichen.csv" is shown
