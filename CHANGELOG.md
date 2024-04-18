# User Management Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]
### Fixed
- Log errors during cas authentication
  - previously exceptions while validating the cas ticket did not get logged which made debugging the cas authentication difficult

## [v1.14.2-1] - 2024-04-02
### Fixed
- Escape username and password in notification email for new users (#128)

## [v1.14.1-1] - 2024-03-12
### Fixed
- Show data synchronization hint only to admins (#127)

## [v1.14.0-1] - 2024-03-11
### Added
- Add email notification support for newly created users via csv import (#125)
- Add Data Synchronisation Detail Component to notify administrative users about cas / dogu behaviour (#119)

### Changed
- Update ces-theme-tailwind to v0.3.8

### Fixed
- Fix column overflow in user import table

## [v1.13.0-1] - 2024-02-29
### Added
- Add ces-theme-tailwind in a second, higher, version (v0.3.7) (#111)
- Add Cloudogu logo for better container crash visibility
- Add descriptions for username / displayname (#114)

### Changed
- Stream all log output to standard out, configure log level (#115)
   - logfiles will neither reside inside the container file system nor a volume
   - add dogu configuration key `logging/root` to control the output log level
- no longer run as root but as unprivileged user (#115, #117)
- update Tomcat to the current version 8.5.99
- update to Java OpenJDK 8.392
- Change app configuration directory from `/var/lib/usermgt/conf` to `/var/lib/usermgt/conf2` (#117)
  - after update to this version you may want to delete `/var/lib/usermgt/conf` with root permissions from your host 
    like this `sudo rm -r /var/lib/ces/usermgt/volumes/data/conf`
  - `/var/lib/usermgt/conf2` will now contain file privileges with the (unprivileged) UID/GID 1000
- Change table in users page to the table in the new theme (#111)
- Change table in groups page to the table in the new theme (#111) 
- Change table in import pages to the table in the new theme (#111) 
- Use Virtual-List-View for querying LDAP with pagination (#112)
  - requires at least v2.6.2-7 of the LDAP-Dogu
- Refactor Pagination-API for users, groups and import-summaries (#112)
- Update the developer guide documentation (#112)

### Fixed
- Fix local development of the backend

## [v1.12.1-1] - 2024-01-11
### Changed
- make givenname mandatory (#109)

## [v1.12.0-1] - 2023-11-13
### Security
- fixed CVE-2023-44483 and some others (#107)

### Changed
- Pages only available to users with manager group will now show an error message if a normal user accesses the page (#105)
- Updated Java base image, org.apache.santuario/xmlsec and com.google.guava/guava (#107)

### Fixed
- Import multiple user (>100) via csv (#101)
- Users without manager group can access restricted areas (#105)
- Pagination for users, groups and import summaries

## [v1.11.0-1] - 2023-09-18
### Added
- Added feature to import users via CSV (#89)

### Changed
- Updated ces-theme-tailwind to 0.2.0 (#89)

### Fixed
- When generating the new .npmrc, the old is now actually removed

## [v1.10.1-1] - 2023-05-02
### Changed
- Update Java-Dependencies to remove CVEs (#87)

## [v1.10.0-2] - 2023-04-21
### Changed
- Update Java-Base-Image (#85)

## [v1.10.0-1] - 2023-04-20
### Changed
- Rewrite frontend with React (#77)
- Extend search endpoint to pass a list of excluded values (#77)
- Update documentation with current screenshots and better explanations (#81)
- Update cypress-version for integration-tests (#78)

### Added
- New integration-tests for rewritten frontend (#78)

## [v1.9.0-1] - 2022-11-14
### Changed
- Add Backend Endpoint for the User Import via CSV-File (#69)
  - For more information see [docs](docs/operations/csv-import_en.md)

## [v1.8.1-1] - 2022-10-20
### Fixed
- in User Managment  an empty Password will not suffice for lowercase and minimum lenght of 9 or less (#67)

## [v1.8.0-1] - 2022-09-28
### Changed
- Prevent system groups (admin/cesManager) from being deleted (#65)
  - This is implemented in both backend and frontend.

## [v1.7.0-1] - 2022-08-23
### Changed
- The password rules are now set via global etcd keys. For more information see [docs](docs/operations/password-policy_en.md#Configuration-of-password-rules-in-etcd) (#63)
  - Note: the existing password rules will NOT be migrated automatically.

## [v1.6.1-2] - 2022-07-05
### Changed
- Increase max username length to 64 characters (was 32 before) (#61)

## [v1.6.1-1] - 2022-05-11
### Security
- java base image
- updated some maven dependencies

## [v1.6.0-2] - 2022-04-29
### Fixed
- Fixed a bug where the pwd-reset checkbox was checked without any effect (#55)

## [v1.6.0-1] - 2022-04-27
Note: CAS version >= 6.5.3-2 is required for this version.

### Added
- Possibility to set the attribute that the user has to change his password at the next login (#51)

## [v1.5.0-3] - 2022-04-26
### Removed
- remove unused source of `/etc/ces/functions.sh` in `startup.sh` (#52)

## [v1.5.0-2] - 2022-04-05
### Changed
- Upgrade java base image to 8u302-1
- Upgrade all packages to get zlib 1.2.12; #49

## [v1.5.0-1] - 2021-12-09
### Added
- add tomcat checksum check in dockerfile

### Changed
- upgrade dogu-build-ib to v1.5.1
- update tomcat to 8.5.73 fixing the following list of vulnerabilities: 
https://tomcat.apache.org/security-8.html#Apache_Tomcat_8.x_vulnerabilities
- valid character inputs in new group dialogue (#47)
- update UI for a more consistent user experience (#46)

### Removed
- remove the ability to edit groups from the user edit view and vice versa (#44)
  - this context switch result in a loss of typed user/group information in the edit view
  
## [v1.4.4-1] - 2021-12-02
### Added
- more detailed messages to enable users to understand if their actions were successful (#41)

## [v1.4.3-1] - 2021-11-17
### Added
- module angular-ui-router-title  and configuration to display individual titels for major views (#39)

## [v1.4.2-1] - 2021-09-03
### Fixed
- Incorrect content type when connecting to the CAS. Changed the content type from `text/xml` to `application/x-www-form-urlencoded` as it supposed to be (#37)

## [v1.4.1-3] - 2021-07-26
### Changed
- Display all options on small screens and at zoom 400% (#32)
- Enhance Color Contrast (#31)
- Make forms more accessible (#34)

### Added
- Added alternative tags for controls (#29)

## [v1.4.1-2] - 2020-12-15
### Added
- Ability to set memory limit via `cesapp edit-config`
- Ability to configure the `MaxRamPercentage` and `MinRamPercentage` for the PlantUML process inside the container via `cesapp edit-conf` (#27)

## [v1.4.1-1] - 2020-10-09
### Fixed
- Fixed bug where no error was shown on invalid password policy or when the endpoint could not be reached
- Fixed misspelled error messages
- Fixed bug where sonar build was not possible for bugfix branch

## [v1.4.0-1] - 2020-10-08
### Added
- added modular makefiles
- implements a configurable password policy option (#19)
### Changed
- update dependencies in package.json
- update maven version
- lint dockerfile in jenkins build
- do shell check in jenkins build
- mark the username field red when trying to add an already existing user
- prevent adding new users with an email address that is already in use by another user (#22)
- prevent changing the email address of a user to an email address that is already in use by another user (#22)
  - existing users cannot update their account until they change their email address, if the address is already taken by another user
- the ldap now also does not allow duplicated mails (see https://github.com/cloudogu/ldap/issues/8)
### Fixed
- fixed broken build
- fixed shellcheck findings in startup.sh
- fixed sonarqube check

## [1.3.0]
### Changed
- using ces-theme now
- removed backup and settings
- changed name to usermgt

## [1.2.0]
### Changed
- do not display remove button on group create view, see `http://192.168.115.124:8080/browse/PESCMMU-194`

## [1.1.1]
### Changed
- do not display remove button on group create view, see `http://192.168.115.124:8080/browse/PESCMMU-194`

## [1.1.0]
### Changed
- use ServiceLoader for loading guice modules, this should drill down package cycles
- SingleSignOut support, see `http://192.168.115.124:8080/browse/PESCMMU-190`
- prevent self remove of users, see `http://192.168.115.124:8080/browse/PESCMMU-183`
- groups can not be removed from the edit view, see `http://192.168.115.124:8080/browse/PESCMMU-181`
- remove hamcreset library from war file
- angular 1.2.26
- resteasy 3.0.10.Final

### Fixed
- fix flickering menu on ui bootstrap

## [1.0.1]
### Changed
- show error page, if ldap is disabled
- update web components

### Fixed 
- fix password validation error message
- fix hardcoded admin role in SecurityModule

## [1.0.0]
### Added
- Initial release
