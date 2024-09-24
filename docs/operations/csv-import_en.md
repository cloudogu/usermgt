# CSV IMPORT

## Call and CSV file default

Users can be imported via the `/users/import` endpoint. CSV according
to [RFC 4180](https://datatracker.ietf.org/doc/html/rfc4180)
is used as import format. The header of the file must define **7** columns:

```csv
username,displayname,givenname,surname,mail,pwdReset,external
```

The order of the columns can vary, but the names of the columns must be kept.

Authentication is performed via the account of the logged-in user. If the user does not have admin rights, the
endpoint cannot be called by the user. Duplicate entries do not affect the result of the import,
but will be processed twice. The result of the import can be used to determine which entry is incorrect.
No groups are currently created or assigned via the import.

## How the import works.

* Any number of users can be created via the import.
* If the user already exists, the values in the CSV are used for updating the user.
* Currently, each user created is considered as internal user.
* Via the import **no** group can be created or assigned.

## EMail Notification

When creating a new user account, users automatically receive an email with their login information, including
username and temporary password. These emails can be configured individually, using placeholders such as ${username}
and ${password}. After the first login, users are prompted to change their temporary password to ensure the security
of the account.

## Result

A result entry is created for the import. This result can be found in the `importHistory` volume under
`/var/lib/usermgt/importHistory`. The result contains a summary of the users that were created or modified.
Furthermore, the result contains possible errors that occurred during the import. Per entry e error code is given:

| Code | Error description                                                                           |
|------|---------------------------------------------------------------------------------------------|
| 1000 | The CSV file is missing columns that are required.                                          |
| 1001 | The import result could not be written to the file system.                                  |
| 2000 | The number of columns in a row does not match those in the header.                          |
| 2001 | A required column was empty.                                                                |
| 2002 | Undefined error when evaluating a row.                                                      |
| 3000 | The row contains a value that has already been assigned.                                    |
| 3001 | An attempt is being made in the line to define a user whose mail has already been assigned. |
| 3002 | The mail address of the user in this row is invalid.                                        |
| 4000 | A column in this row had an undefined format error.                                         |
| 4001 | A column in this row had more than 128 characters.                                          |
| 4002 | A column in this row had less than 2 characters.                                            |
| 4003 | A column in this row contained invalid characters.                                          |

Translated with DeepL.com (free version)

In addition to the volume, summaries of the imports can be accessed via the `/users/import/summaries` endpoint.
Individual results are available via the endpoint `/users/import/{importID}` and can be downloaded via
`/users/import/{importID}/download`.

## Fully usable CSV file

```csv
username,displayname,givenname,surname,mail,pwdReset,external
dent,Arthur Dent,Arthur,Dent,arthur.dent@hitchhiker.com,false,true
trillian,Tricia McMillan,Tricia,McMillan,tricia.mcmillan@hitchhiker.com,false,true
```
