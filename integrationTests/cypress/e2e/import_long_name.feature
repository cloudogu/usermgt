Feature: Tests for uploading file with user name that is too long (> 128 characters)

  Background:
    Given the user "manager" exists
    And the user "manager" is member of the group "cesManager"
    And the file "userimport_mehr128_Zeichen.csv" is uploaded
    And the user "manager" with password "newuserpassword1234A$" is logged in

  Scenario: a user selects a file for upload
    When the user opens the user import page
    And the user selects the file "userimport_mehr128_Zeichen.csv"
    Then a table of the file content for the file "userimport_mehr128_Zeichen.csv" is displayed

  @clean_user_import
  @clear_downloadDir
  Scenario: a user uploads a file
    When the user opens the user import details page
    Then the user import page shows an import with the message "Import failed!" and the details "Skipped data rows (1)"

  @clean_user_import
  @clear_downloadDir
  Scenario: after uploading a file a user inspects the user import details page about the skipped accounts
    When the user opens the user import details page
    And the user clicks on the details regarding the "skipped" user import
    Then the table shows the error message "The following columns do not match the default format: 'username'."

  @clean_user_import
  @clear_downloadDir
  Scenario: after uploading a file a user downloads the import overview
    When the user opens the user import details page
    And the user downloads the import overview
    Then the import result is downloaded and contains information regarding "0" created, "0" updated and "1" skipped accounts in the file "userimport_mehr128_Zeichen.csv"

  @clean_user_import
  @clear_downloadDir
  Scenario: after uploading a file a user inspects the users page
    When the user opens the users page
    Then the new user "jjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjj" was not added

  @clean_user_import
  @clear_downloadDir
  Scenario: after uploading a file a user inspects the user import summaries page
    When the user opens the user import summaries page
    Then a table with the import information "New: 0, Updated: 0, Skipped: 1" regarding the file "userimport_mehr128_Zeichen.csv" is shown
