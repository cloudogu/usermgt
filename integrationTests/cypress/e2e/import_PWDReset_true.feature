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
   @clear_mails
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

   Scenario: after uploading a file a user inspects the user import summaries page
     When the user opens the user import summaries page
     Then a table with the import information "New: 1, Updated: 0, Skipped: 0" regarding the file "tap_userimport_pwd_t.csv" is shown

   @clean_user_import
   Scenario: after the user import the newly created user receives an email
     Then the user "testUser" receives an email with his user details

   Scenario: a newly created user tries to log in for the first time
      When the user logs out by visiting the cas logout page
      And the user "testUser" tries to log in with his generated password
      Then the newly created user is asked to change his password

   Scenario: a newly created user logs in for the first time
      When the user "testUser" tries to log in with his generated password
      And the user sets the new password to "testuserpassword1234A$"
      And the user "testUser" with password "testuserpassword1234A$" logs in
      Then the account page for user "testUser" is shown
      And users, groups, user import and import overview should not be visible in the navbar
