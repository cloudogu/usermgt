Feature: Tests for inspecting the result details of an user import

 Background:
     Given the user "manager" exists
     And the user "manager" is member of the group "cesManager"
     And the user "manager" with password "newuserpassword1234A$" is logged in

   Scenario: a user selects a file for upload
     When the user opens the user import page
     And the user selects the file "userimport_keineParameter.csv"
     Then a table of the file content for the file "userimport_keineParameter.csv" is displayed

   @clean_before
   @clear_downloadDir
   Scenario: a user uploads a file
     When the user opens the user import page
     And the user uploads the file "userimport_keineParameter.csv"
     Then the user import page shows an import with the message "Import failed!" and the details "Skipped data rows (7)"

   Scenario: after uploading a file a user inspects the user import details page about the skipped accounts
       When the user opens the user import details page
       And the user clicks on the details regarding the "skipped" user import
       Then the table shows the error message "<message>"

       Examples:
        | message                                                           |
        | The following columns are required but were empty: 'displayname'. |
        | The following columns had an invalid data type: 'external'.       |
        | The following columns are required but were empty: 'mail'.        |
        | The following columns had an invalid data type: 'pwdReset'.       |
        | The following columns are required but were empty: 'surname'.     |
        | The following columns are required but were empty: 'username'.    |
        | The following columns are required but were empty: 'givenname'.   |

   Scenario: after uploading a file a user inspects the users page
     When the user opens the users page
     Then the new user " " was not added

   @clean_user_import
   Scenario: after uploading a file a user inspects the user import summaries page
     When the user opens the user import summaries page
     Then a table with the import information "New: 0, Updated: 0, Skipped: 1" regarding the file "userimport_keineParameter.csv" is shown
