Feature: Tests for inspecting the result details of an user import

 Background:
     Given the user "manager" exists
     And the user "manager" is member of the group "cesManager"
     And the user "manager" with password "newuserpassword1234A$" is logged in

   @clean_before
   @clear_downloadDir
   @add_user
   Scenario Outline: a user uploads a file
     When the user opens the user import page
     And the user uploads the file "userimport6.csv"
     Then the user import page shows an import with the message "<message>" and the details "<details>"

     Examples:
      | message                        | details               |
      | Import completed with errors!  | Created accounts (2)  |
      | Import completed with errors!  | Updated accounts (1)  |
      | Import completed with errors!  | Skipped data rows (1) |

   @clean_before
   @clear_downloadDir
   @clean_user_import
   Scenario: after uploading a file a user inspects the user import summaries page
     When the user opens the user import page
     And the user uploads the file "userimport6.csv"
     And the user opens the user import summaries page
     Then a table with the import information "New: 2, Updated: 1, Skipped: 1" regarding the file "userimport6.csv" is shown

   @clean_before
   @clear_downloadDir
   @clean_user_import
   Scenario: after uploading a file a user can delete, download or show details of the import entry at the import summaries page
     When the user opens the user import page
     And the user uploads the file "userimport6.csv"
     And the user opens the user import summaries page
     And the user opens the menu in the functions column
     Then the import summaries page offers the possibility to delete, download or show details of the import entry

   @clean_before
   @clear_downloadDir
   @clean_user_import
   Scenario Outline: after uploading a file a user inspects the user import details page about the created accounts
       When the user opens the user import page
       And the user uploads the file "userimport6.csv"
       And the user opens the user import details page
       And the user clicks on the details regarding the "created" user import
       Then the table shows the information about the "created" user "<username>"

       Examples:
        | username     |
        | maxtest      |
        | mariatest    |

   @clean_before
   @clear_downloadDir
   @clean_user_import
   @add_user
   Scenario: after uploading a file a user inspects the user import details page about the updated accounts
       When the user opens the user import page
       And the user uploads the file "userimport6.csv"
       And the user opens the user import details page
       And the user clicks on the details regarding the "updated" user import
       Then the table shows the information about the "updated" user "mmustermann"

   @clean_before
   @clear_downloadDir
   @clean_user_import
   Scenario: after uploading a file a user inspects the user import details page about the skipped accounts
       When the user opens the user import page
       And the user uploads the file "userimport6.csv"
       And the user opens the user import details page
       And the user clicks on the details regarding the "skipped" user import
       Then the table shows the error message "The following columns contained a value that is marked as unique and was already assigned: 'mail'."
