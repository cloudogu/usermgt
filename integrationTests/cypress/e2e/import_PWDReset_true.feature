Feature: Tests for uploading file with user that has password reset enabled

 Background:
     Given the user "manager" exists
     And the user "manager" is member of the group "cesManager"
     And the user "manager" with password "newuserpassword1234A$" is logged in

   Scenario: a user selects a file for upload
     When the user opens the user import page
     And the user selects the file "tap_userimport_pwd_t.csv"
     Then a table of the file content for the file "tap_userimport_pwd_t.csv" is displayed

   @clean_before
   @clear_downloadDir
   Scenario: a user uploads a file
     When the user opens the user import page
     And the user uploads the file "tap_userimport_pwd_t.csv"
     Then the user import page shows an import with the message "Import successfully completed!" and the details "Created accounts (1)"

   Scenario: after uploading a file a user inspects the user import details page
     When the user opens the user import details page
     And the user clicks on the details regarding the "created" user import
     Then the table shows the information about the "created" user "testUser"

   Scenario: after uploading a file a user downloads the import overview
     When the user opens the user import details page
     And the user downloads the import overview
     Then the import result is downloaded and contains information regarding "1" created, "0" updated and "0" skipped accounts in the file "tap_userimport_pwd_t.csv"

   Scenario: after uploading a file a user inspects the users page
     When the user opens the users page
     Then the new user "testUser" was added

   Scenario: after uploading a file an imported user can be edited
     When the user opens the users page
     And the user clicks on the edit-user button for the user "testUser"
     Then the password reset flag is checked

   @clean_user_import
   Scenario: after uploading a file a user inspects the user import summaries page
     When the user opens the user import summaries page
     Then a table with the import information "New: 1, Updated: 0, Skipped: 0" regarding the file "tap_userimport_pwd_t.csv" is shown
