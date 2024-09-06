Feature: Tests for deleting an user import after its upload

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

   Scenario: after uploading a file a user can delete, download or show details of the import entry at the import summaries page
     When the user opens the user import summaries page
     And the user opens the menu in the functions column
     Then the import summaries page offers the possibility to delete, download or show details of the import entry

   Scenario: a user deletes the user import
    When the user opens the user import summaries page
    And deletes the entry for the user import
    Then the entry is removed and a message regarding the successful deletion of the entry is shown

   Scenario Outline: after deleting the user import a user inspects the users page regarding the added users
     When the user opens the users page
     Then the new user "<username>" was added

     Examples:
      | username     |
      | maxtest      |
      | mariatest    |

   Scenario: after deleting the user import a user inspects the users page regarding the updated users
     When the user opens the users page
     Then the user "mmustermann" has his mail updated to "mark@test.de" and his display name to "Mark Test"
