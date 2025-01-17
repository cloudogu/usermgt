# Configuration for integration tests

The integration tests expect a certain configuration to run successfully. Specifically certain values be set in the
etcd. These are as follows:

```bash
etcdctl set /config/_global/password-policy/must_contain_capital_letter true
etcdctl set /config/_global/password-policy/must_contain_lower_case_letter true
etcdctl set /config/_global/password-policy/must_contain_digit true
etcdctl set /config/_global/password-policy/must_contain_special_character true
etcdctl set /config/_global/password-policy/min_length 14
```

In order for the set values to be taken into account, the Dogu must be restarted once.

The values configure the password rules that are checked in the integration tests.

## Prerequisites

* It is necessary to install the program `yarn`.
* As some tests involve sending emails, Mailhog must be present in the CES beforehand. The [[instructions for sending emails locally (https://docs.cloudogu.com/en/docs/dogus/postfix/development/Send_Mails_locally/)]] in the postfix dogu can be used for this purpose. <!-- markdown-link-check-disable-line -->

## Configuration

To ensure that all integration tests work properly, some data must be configured beforehand.

**integrationTests/cypress.json** [[Link to file](../../integrationTests/cypress.config.ts)] <!-- markdown-link-check-disable-line -->

1) The base URL must be adapted to the host system.
   - To do this, the `baseUrl` field must be adapted to the host FQDN.
2) Further aspects must be configured.
   These are used as environment variables in the `cypress.json` to make the CES administrator findable in your own system:
   - `DoguName` - Determines the name of the current Dogus and is used for routing.
   - `AdminUsername` - The username of the CES admin.
   - `AdminPassword` - The password of the CES admin.
   - `AdminGroup` - The user group for CES administrators.

An example `cypress.config.ts` looks like this:
```json
{
  "...": "...other values...",
  "baseUrl": "https://192.168.56.2",
  "env": {
    "DoguName": "cas/login",
    "MaxLoginRetries": 3,
    "AdminUsername": "ces-admin",
    "AdminPassword": "ecosystem2016",
    "AdminGroup": "CesAdministrators" 
  }
}
```

## Starting the integration tests

The integration tests can be started in two ways:

1. if necessary, run `yarn install` so that Cypress is available
2. with `yarn cypress run` the tests start only in the console without visual feedback.
   This mode is helpful if the execution is in the foreground.
   For example, with a Jenkins pipeline.
2. `yarn cypress open` starts an interactive window where you can execute, visually observe and debug the tests.
   This mode is particularly helpful when developing new tests and finding errors.

If an error based on the `badeball/cypress-cucumber-preprocessor` library appears in the Cypress UI (`yarn cypress open`) and refers to "Experimental Run All", then it is advisable to click through the tests individually. This may be a problem with the execution methodology that does not take place on the console (`yarn cypress run`).
