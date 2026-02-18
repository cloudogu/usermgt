# Release Notes

Below you will find the release notes for User Management. 

Technical details on a release can be found in the corresponding [Changelog](https://docs.cloudogu.com/en/docs/dogus/usermgt/CHANGELOG/).

## [Unreleased]

## [v1.20.1-4] - 2026-02-18
- We have only made technical changes. You can find more details in the changelogs.

## [v1.20.1-3] - 2026-02-13
### Security
- [#206] fixed [cve-2025-68121](https://avd.aquasec.com/nvd/2025/cve-2025-68121/)
- [#206] fixed [cve-2026-24515](https://avd.aquasec.com/nvd/2026/cve-2026-24515/)

## [v1.20.1-2] - 2026-01-29
### Security
- [#204] fixed [cve-2025-15467](https://avd.aquasec.com/nvd/2025/cve-2025-15467/)

## [v1.20.1-1] - 2025-12-12
- Errors that were incorrectly reported by the user REST API as Authentication errors are now correctly reported as internal server errors . For instance, these could be lack of CAS connectivity, throttling, or LDAP timeouts.

## [v1.20.0-5] - 2025-04-25
### Changed
- Usage of memory and CPU was optimized for the Kubernetes Multinode environment.

## [v1.20.0-4] - 2025-04-10
### Security
* This release fixes the critical security vulnerability [CVE-2025-24813](https://nvd.nist.gov/vuln/detail/CVE-2025-24813).
  An update is therefore recommended.

## [v1.20.0-3] - 2025-02-21
### Changed
- Renamed table headline key "from" Date to "Date of import"

## [v1.20.0-2] - 2025-02-13
We have only made technical changes. You can find more details in the changelogs.

## [v1.20.0-1] - 2025-01-27
We have only made technical changes. You can find more details in the changelogs.

## [v1.19.0-1] - 2025-01-22
* All mandatory fields in the form for creating/editing users are now marked as mandatory and it is no longer possible to submit the form
  no longer possible to submit the form if not all mandatory fields are filled in

## [v1.18.0-1] - 2025-01-17
### Changed
* The internal makefiles have been updated to standardize the versioning of the release notes.
### Added
* Optional sync query parameter to create user endpoint
  * If supplied, users and groups will be added synchronously to avoid unauthorized errors
  * For usage see create_users_synced.md

## Release 1.17.2-1
* Mail-To links are no longer displayed in the user list

## Release 1.17.1-1
* Bug fix: In 1.17.0-1, non-external users are also incorrectly marked as external users. From this version onwards, only external users are indicated as external users.

## Release 1.17.0-1
* External users are marked as such in the overview
    * There is an additional column in the user overview table that shows whether a user is external or internal
        * This column is only displayed if there is at least one external user
    * External users cannot be edited. All fields are either hidden or deactivated
    * Groups can still be added to and removed from external users
  
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
