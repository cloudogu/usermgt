# CSV IMPORT

## Call and CSV file default

Users can be imported via the endpoint `/users/import`.
CSV is used as import format. The header of the file must define at least **6** columns.
Recommended is:
``csv
Username;FirstName;Surname;DisplayName;Email;Groups
```
This is the order in which the values are read. Only the order is important, the values in the first 
first column can be chosen freely. 
Therefore these columns could also be in German, for example:
``csv
user name;first name;last name;display title;mail;group
```

The authentication runs over the account of the logged in user. If the user has no manager rights, this endpoint cannot be called by the user.
endpoint cannot be called by the user. Duplicate entries are filtered out and the protocol can be used to detect
can be determined if an entry is incorrect. Groups are only assigned if they already exist in the system.
No new groups will be created automatically during this process.

## How the import works.

* Any number of users can be created via the import.
* Via the import a group can **not** be created.
* Via the import, a new or existing user can be assigned to a group.
  * To add an existing user to a group/, only the user name and the groups must be written in the line
    line.
  * Example: `Tester3;;;;G1,G2,G3,G4`

## Reason for necessity

If there are several new employees or the CES is initially set up in a company, several user accounts must be created.
user accounts need to be created. To save the administrators, respectively the managers of the CES, the effort of manual
manual creation. The users can be listed compactly and efficiently via the import and in after a short moment all users are created.

## Protocol

A protocol entry is created for the import. This protocol is stored in the volume user-import-protocol under
`/var/lib/usermgt/protocol/user-import-protocol`. For each user and for each group assignment a
entry is created about the status of the process. The status can be successful, incomplete or already exists.

## Email

For every created user a mail with his user data will be sent to him. In the configuration file of the UserMgt
the `host` and `port` can be defined. To customize the subject and content of the auto generated emails you can set the 
configuration keys `import/mail/subject` and `import/mail/content`.

## Fully usable CSV file
`csv
Username;FirstName;Surname;DisplayName;Email;Groups
Tester1;Tes;Ter;Tester1;test1@test.com;G1,G2   
Tester2;Tes;Ter2;Tester2;test2@test.com;G2,G3
Tester3;Tes;Ter3;Tester3;test3@test.com;G1,G3
Tester4;Tes;Ter4;Tester4;test4@test.com;G4,G1
```

Translated with www.DeepL.com/Translator (free version)