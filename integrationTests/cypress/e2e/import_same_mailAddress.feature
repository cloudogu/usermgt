Feature: Tests for uploading file with users with same mail address

 Background:
     Given the user "manager" exists
     And the user "manager" is member of the group "cesManager"
     And the file "tap_userimport_mailpara.csv" is uploaded
     And the user "manager" with password "newuserpassword1234A$" is logged in

   Scenario: a user selects a file for upload
     When the user opens the user import page
     And the user selects the file "tap_userimport_mailpara.csv"
     Then a table of the file content for the file "tap_userimport_mailpara.csv" is displayed

   @clean_before
   @clear_downloadDir
   @clear_mails
   @clean_user_import
   Scenario Outline: a user uploads a file
     When the user opens the user import details page
     Then the user import page shows an import with the message "<message>" and the details "<details>"

     Examples:
      | message                        | details               |
      | Import completed with errors!  | Created accounts (4)  |
      | Import completed with errors!  | Skipped data rows (1) |

   @clean_before
   @clear_downloadDir
   @clear_mails
   @clean_user_import
   Scenario Outline: after uploading a file a user inspects the user import details page about the created accounts
     When the user opens the user import details page
     And the user clicks on the details regarding the "created" user import
     Then the table shows the information about the "created" user "<username>"

     Examples:
      | username     |
      | Testertest   |
      | Mustertest   |
      | Neutest      |
      | Haustest     |

   @clean_before
   @clear_downloadDir
   @clear_mails
   @clean_user_import
   Scenario: after uploading a file a user inspects the user import details page about the skipped accounts
     When the user opens the user import details page
     And the user clicks on the details regarding the "skipped" user import
     Then the table shows the error message "A user with the email 'testmail@cloudogu.de' already exists."

   @clean_before
   @clear_downloadDir
   @clear_mails
   @clean_user_import
   Scenario: after uploading a file a user downloads the import overview
     When the user opens the user import details page
     And the user downloads the import overview
     Then the import result is downloaded and contains information regarding "4" created, "0" updated and "1" skipped accounts in the file "tap_userimport_mailpara.csv"

   @clean_before
   @clear_downloadDir
   @clear_mails
   @clean_user_import
   Scenario Outline: after uploading a file a user inspects the users page
     When the user opens the users page
     Then the new user "<username>" was added

     Examples:
      | username     |
      | Testertest   |
      | Mustertest   |
      | Neutest      |
      | Haustest     |

   @clean_before
   @clear_downloadDir
   @clear_mails
   @clean_user_import
   Scenario: after uploading a file a user inspects the user import summaries page
     When the user opens the user import summaries page
     Then a table with the import information "New: 4, Updated: 0, Skipped: 1" regarding the file "tap_userimport_mailpara.csv" is shown

   @clean_before
   @clear_downloadDir
   @clear_mails
   @clean_user_import
   Scenario: after the user import the newly created user receives an email
     Then the user "Testertest" receives an email with his user details

   @clean_before
   @clear_downloadDir
   @clear_mails
   Scenario: a newly created user tries to log in for the first time
      When the user logs out by visiting the cas logout page
      And the user "Testertest" tries to log in with his generated password
      Then the user is asked to change his password
