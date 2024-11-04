Feature: Tests for updating user that has password reset enabled

 Background:
     Given the user "manager" exists
     And the user "manager" is member of the group "cesManager"
     And the user "manager" with password "newuserpassword1234A$" is logged in

   @clean_before
   @clear_downloadDir
   @clear_mails
   @clean_user_import
   Scenario: a user uploads a file
     When the user opens the user import page
     And the user uploads the file "tap_userimport_akt_f.csv"
     Then the user import page shows an import with the message "Import successfully completed!" and the details "Created accounts (1)"

   @clean_before
   @clear_downloadDir
   @clear_mails
   Scenario: a newly created user logs in for the first time
     When the user opens the user import page
     And the user uploads the file "tap_userimport_akt_f.csv"
     And the user opens the user import page
     And the user uploads the file "tap_userimport_akt3_f.csv"
     And the user "Testertest" tries to log in with his generated password
     And the user sets the new password to "testuserpassword1234A$"
     And the user "Testertest" with password "testuserpassword1234A$" logs in
     Then the account page for user "Testertest" is shown

   @clean_before
   @clear_downloadDir
   @clear_mails
   @clean_user_import
   Scenario: a user selects an updated file for upload
     When the user opens the user import page
     And the user uploads the file "tap_userimport_akt_f.csv"
     And the user opens the user import page
     And the user selects the file "tap_userimport_akt3_f.csv"
     Then a table of the file content for the file "tap_userimport_akt3_f.csv" is displayed

   @clean_before
   @clear_downloadDir
   @clear_mails
   @clean_user_import
   Scenario: a user uploads an updated file
     When the user opens the user import page
     And the user uploads the file "tap_userimport_akt_f.csv"
     And the user opens the user import page
     And the user uploads the file "tap_userimport_akt3_f.csv"
     Then the user import page shows an import with the message "Import successfully completed!" and the details "Updated accounts (1)"

   @clean_before
   @clear_downloadDir
   @clear_mails
   @clean_user_import
   Scenario: after uploading a file a user downloads the import overview
     When the user opens the user import page
     And the user uploads the file "tap_userimport_akt_f.csv"
     And the user opens the user import page
     And the user uploads the file "tap_userimport_akt3_f.csv"
     And the user opens the user import details page
     And the user downloads the import overview
     Then the import result is downloaded and contains information regarding "0" created, "1" updated and "0" skipped accounts in the file "tap_userimport_akt3_f.csv"

   @clean_before
   @clear_downloadDir
   @clear_mails
   @clean_user_import
   Scenario: after uploading a file a user inspects the users page
     When the user opens the user import page
     And the user uploads the file "tap_userimport_akt_f.csv"
     And the user opens the user import page
     And the user uploads the file "tap_userimport_akt3_f.csv"
     And the user opens the users page
     Then the new user "Testertest" was added

   @clean_before
   @clear_downloadDir
   @clear_mails
   @clean_user_import
   Scenario: after uploading a file an imported user can be edited
     When the user opens the user import page
     And the user uploads the file "tap_userimport_akt_f.csv"
     And the user opens the user import page
     And the user uploads the file "tap_userimport_akt3_f.csv"
     And the user opens the users page
     And the user clicks on the edit-user button for the user "Testertest"
     Then the password reset flag is checked

   @clean_before
   @clear_downloadDir
   @clear_mails
   @clean_user_import
   Scenario: after uploading a file a user inspects the user import summaries page
     When the user opens the user import page
     And the user uploads the file "tap_userimport_akt_f.csv"
     And the user opens the user import page
     And the user uploads the file "tap_userimport_akt3_f.csv"
     And the user opens the user import summaries page
     Then a table with the import information "New: 0, Updated: 1, Skipped: 0" regarding the file "tap_userimport_akt3_f.csv" is shown

   @clean_before
   @clear_downloadDir
   @clear_mails
   Scenario: an updated user tries to log in again
     When the user opens the user import page
     And the user uploads the file "tap_userimport_akt_f.csv"
     And the user logs out by visiting the cas logout page
     And the user "Testertest" tries to log in with his generated password
     And the user sets the new password to "testuserpassword1234A$"
     And the user "manager" with password "newuserpassword1234A$" logs in
     And the user opens the user import page
     And the user uploads the file "tap_userimport_akt3_f.csv"
     And the user logs out by visiting the cas logout page
     And the user "Testertest" with password "testuserpassword1234A$" logs in
     Then the user is asked to change his password
