# User Management Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

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
