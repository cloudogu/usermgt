Feature: Tests for download user import result

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

   Scenario: after uploading a file a user inspects the user import summaries page
     When the user opens the user import summaries page
     Then a table with the import information "New: 2, Updated: 1, Skipped: 1" regarding the file "userimport6.csv" is shown

   @clean_user_import
   Scenario: after uploading a file a user downloads the import overview
     When the user opens the user import details page
     And the user downloads the import overview
     Then the import result is downloaded and contains information regarding "2" created, "1" updated and "1" skipped accounts in the file "userimport6.csv"
