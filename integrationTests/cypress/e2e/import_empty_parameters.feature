Feature: Tests for inspecting the result details of an user import

 Background:
     Given the user "manager" exists
     And the user "manager" is member of the group "cesManager"
     And the file "userimport_keineParameter.csv" is uploaded
     And the user "manager" with password "newuserpassword1234A$" is logged in

   Scenario: a user selects a file for upload
     When the user opens the user import page
     And the user selects the file "userimport_keineParameter.csv"
     Then a table of the file content for the file "userimport_keineParameter.csv" is displayed

   @clean_before
   @clear_downloadDir
   @clean_user_import
   Scenario: a user uploads a file
     When the user opens the user import details page
     Then the user import page shows an import with the message "Import failed!" and the details "Skipped data rows (7)"

   @clean_before
   @clear_downloadDir
   @clean_user_import
   Scenario: after uploading a file a user inspects the user import details page about the skipped accounts
       When the user opens the user import details page
       And the user clicks on the details regarding the "skipped" user import
       Then the table shows the error message "<message>"

       Examples:
        | message                                      |
        | The column 'displayname' must be filled in.  |
        | The column 'external' has an invalid format. |
        | The column 'mail' must be filled in.         |
        | The column 'pwdReset' has an invalid format. |
        | The column 'surname' must be filled in.      |
        | The column 'username' must be filled in.     |
        | The column 'givenname' must be filled in.    |

   @clean_before
   @clear_downloadDir
   @clean_user_import
   Scenario: after uploading a file a user inspects the users page
     When the user opens the users page
     Then the new user " " was not added

   @clean_before
   @clear_downloadDir
   @clean_user_import
   Scenario: after uploading a file a user inspects the user import summaries page
     When the user opens the user import summaries page
     Then a table with the import information "New: 0, Updated: 0, Skipped: 1" regarding the file "userimport_keineParameter.csv" is shown
