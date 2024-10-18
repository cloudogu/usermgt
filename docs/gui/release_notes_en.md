# Release Notes

Below you will find the release notes for User Management. 

Technical details on a release can be found in the corresponding [Changelog](https://docs.cloudogu.com/en/docs/dogus/usermgt/CHANGELOG/).

## Release 1.16.4-1
* On the user import preview page there are now 25 entries per page instead of the previously 8 entries
* Mails with more than one ‘@’ are no longer allowed

## Release 1.16.3-1
* The errors of the user import results are now correctly sorted in ascending order by line number.

## Release 1.16.2-1
* Correction of a spelling error in the error messages of the import

## Release 1.16.1-1
* Improved some error messages in the CSV import process.

## Release 1.16.0-1
*Relicense own code to AGPL-3-only

## Release 1.15.4-1
* Improved user-friendliness of the user import
    * The error messages in the import overviews are now more meaningful and more similar to those during normal user creation

## Release 1.15.3-1
* Improved some error messages in both manual editing and CSV import processes.
* The page headline in which users can change their own data ("Account") renames to "My Account".
* Labels for associating users with groups are renamed in such a way to avoid the impression that new accounts or groups might be created.

## Release 1.15.2-1
* Fix of critical CVE CVE-2024-41110 in library dependencies. This vulnerability could not be actively exploited in User Management, though.

## Release 1.15.1-1
* Optional fields are now marked as "optional" when creating or editing accounts or groups.

## Release 1.15.0-1

We have only made technical changes. You can find more details in the changelogs.

## Release 1.14.3-2

* Improved usability: When creating or editing groups or accounts, feedback is given when saving if these are incorrect.

## Release 1.14.3-1

* Extension of mail validation: Mail addresses with numbers can now also be stored for accounts.
* Adaptation of group management: Groups may not contain spaces. Due to old versions, groups with spaces may exist in the system. These can now be edited again.
