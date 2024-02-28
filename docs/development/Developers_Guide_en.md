# Local development

The backend and frontend of the Usermgt can be developed locally.

## Develop the backend locally

The following requirements must be met to develop the Usermgt backend:

- Install Oracle JDK / Open JDK 8
- Install Maven (use mvn -version to check whether jdk 8 is set up correctly / if not, change JAVA_HOME)
- Install Docker

To start or debug the Usermgt backend locally, a connection to an LDAP is required.
This LDAP can either be operated locally in a Docker container, or the LDAP from the CES can be used.

### Setting up a local LDAP in a Docker container

The following steps are required to start the LDAP in the Docker container:

1. check out the repository: https://github.com/cloudogu/docker-sample-ldap
2. build the container: `docker build -t usermgt/ldap .`
3. start the container: `docker run --rm -p 389:389 usermgt/ldap`.
4. enter the LDAP configuration for the backend in the file [`app/env/data/ldap.xml`](../../app/env/data/ldap.xml): <!-- markdown-link-check-disable-line -->
    ```xml
    <?xml version="1.0" encoding="UTF-8"?>
    <ldap>
        <host>localhost</host>
        <port>389</port>
    
        <!-- LDAP User & Password -->
        <bind-dn>cn=usermgt_x53eMC,ou=Special Users,o=ces.local,dc=cloudogu,dc=com</bind-dn>
        <bind-password>dykIuJz9eQzylL9HLNp4xy+fjPGsNsqvzulBE7iYtMqnvusmvG6Jc4aWKTtImTxz</bind-password>
        
        <user-base-dn>ou=people,o=ces.local,dc=cloudogu,dc=com</user-base-dn>
        <group-base-dn>ou=Groups,o=ces.local,dc=cloudogu,dc=com</group-base-dn>
    
        <disable-member-listener>true</disable-member-listener>
        <disabled>false</disabled>
    </ldap>
    ```
   > The users and passwords of the LDAP container are stored in can be found in the [README](https://github.com/cloudogu/docker-sample-ldap/blob/master/README.md).

   > The password must be encrypted. For this purpose, the [cipher.sh](../../app/src/main/webapp/WEB-INF/cipher.sh) can be used. <!-- markdown-link-check-disable-line -->

### Using the LDAP from the CES

To use the LDAP from the CES for the local backend of the Usermgt, the following steps are necessary:

1. make the port of the LDAP from the CES available.
   There are two options here:
    - Make the port available from the running container. For example with
      this [instruction](https://stackoverflow.com/questions/19335444/how-do-i-assign-a-port-mapping-to-an-existing-docker-container)
    - Expose the port of the LDAP via the `dogu.json`:
      Add the following entry to the `dogu.json` of the LDAP-Dogus:
      ```json
      "ExposedPorts": [
        {
          "Type": "tcp",
          "Host": 389,
          "Container": 389
        }
      ]
      ```
      Rebuild and start the LDAP-Dogu with `cesapp build ldap`.
2. read the LDAP configuration from the usermgt logu of the CES
   read out: `docker exec -it usermgt cat /var/lib/usermgt/conf/ldap.xml`.
3. enter the LDAP configuration for the backend in the file [`app/env/data/ldap.xml`](../../app/env/data/ldap.xml): <!-- markdown-link-check-disable-line -->
    ```xml
    <?xml version="1.0" encoding="UTF-8"?>
    <ldap>
      <!-- Enter the IP of the local CES-->
      <host>192.168.56.2</host>
      <port>389</port>
      <bind-dn>cn=usermgt_lQURMd,ou=Special Users,o=ces.local,dc=cloudogu,dc=com</bind-dn>
      <bind-password>wTyqbtiV9DdZvs0CCs8NU4MMmiRztny4PJt1sSvjz2G5zC2OVwWOoTA+Bj1R2rcE</bind-password>
      <user-base-dn>ou=people,o=ces.local,dc=cloudogu,dc=com</user-base-dn>
      <group-base-dn>ou=Groups,o=ces.local,dc=cloudogu,dc=com</group-base-dn>
      <disabled>false</disabled>
    </ldap>
    ```
   > The password is already encrypted and can be used.

### Start the Usermgt backend locally

So that the Usermgt backend can be used locally without a CAS, the environment variable `UNIVERSEADM_STAGE` must be set to the value `DEVELOPMENT`.

```shell
export UNIVERSEADM_STAGE=DEVELOPMENT`
```

The backend can then be started as follows:

- Change to the `app` directory: `cs app`
- Create the project: `mvn clean install`
- Build and start the project: `mvn -DskipTests -P-webcomponents package jetty:run-war`

> Only the backend is rebuilt and started, the frontend is not created, as the Maven profile `webcomponents` is ignored.

The backend is accessible under the URL `http://localhost:8084/usermgt/api`

> The basic authentication in development mode is `User: admin | Password: admin`.

## Develop the frontend locally

The frontend of Usermgt can be developed locally either with a mock backend or with the local backend of Usermgt.

### Start mock backend

The mock backend can be started with the following command:

```
cd app/src/main/ui
yarn backend
```

### Start local dev backend

The local dev backend can be set up and started as [described above](#develop-the-backend-locally).

### Start frontend

The file `.env.local` must be created so that the local frontend can authenticate with the backend.
To do this, the file [`app/src/main/ui/.env`](../../app/src/main/ui/.env) can be copied as `app/src/main/ui/.env.local`. <!-- markdown-link-check-disable-line -->
The credentials of the local backend (`User: admin | Password: admin`) are then entered there.

The frontend can then be started with the following command.
```
cd app/src/main/ui
yarn install
yarn dev
```

## Create test data for local development

Generated test data can be imported for local development.

### Test user data

- Create users: `create_users.py <number of users>`

If the script is called without parameters, 5 users are created. The count always starts at 0.
If a data conflict occurs, the script continues anyway.

Example: Create 10 users
```shell
docs/development/create_users.py 10
```

### Test group data

- Create groups: ``create_groups.py <number of groups>`

If the script is called without parameters, 5 users are created. The count always starts at 0.
If a data conflict occurs, the script continues anyway.

Example: Create 10 groups
```shell
docs/development/create_groups.py 10
```

## Shell testing with BATS

You can create and amend bash tests in the `unitTests` directory. The make target `unit-test-shell` will support you with a generalized bash test environment.

```bash
make unit-test-shell
```

BATS is configured to leave JUnit compatible reports in `target/shell_test_reports/`.

In order to write testable shell scripts these aspects should be respected:

### Global environment variable `STARTUP_DIR`

The global environment variable `STARTUP_DIR` will point to the directory where the production scripts (aka: scripts-under-test) reside. Inside the dogu container this is usually `/`. But during testing it is easier to put it somewhere else for permission reasons.

A second reason is that the scripts-under-test source other scripts. Absolute paths will make testing quite hard. Source new scripts like so, in order that the tests will run smoothly:

```bash
source "${STARTUP_DIR}"/util.sh
```

Please note in the above example the shellcheck disablement comment. Because `STARTUP_DIR` is wired into the `Dockerfile` it is considered as global environment variable that will never be found unset (which would soon be followed by errors).

Currently sourcing scripts in a static manner (that is: without dynamic variable in the path) makes shell testing impossible (unless you find a better way to construct the test container)

### General structure of scripts-under-test

It is rather uncommon to run a _scripts-under-test_ like `startup.sh` all on its own. Effective unit testing will most probably turn into a nightmare if no proper script structure is put in place. Because these scripts source each other _AND_ execute code **everything** must be set-up beforehand: global variables, mocks of every single binary being called... and so on. In the end the tests would reside on an end-to-end test level rather than unit test level.

The good news is that testing single functions is possible with these little parts:

1. Use sourcing execution guards
1. Run binaries and logic code only inside functions
1. Source with (dynamic yet fixed-up) environment variables

#### Use sourcing execution guards

Make sourcing possible with _sourcing execution guards._ like this:

```bash
# yourscript.sh
function runTheThing() {
    echo "hello world"
}

if [[ "${BASH_SOURCE[0]}" == "${0}" ]]; then
  runTheThing
fi
```

The `if`-condition below will be executed if the script is executed by calling via the shell but not when sourced:

```bash
$ ./yourscript.sh
hello world
$ source yourscript.sh
$ runTheThing
hello world
$
```

Execution guards work also with parameters:

```bash
# yourscript.sh
function runTheThing() {
    echo "${1} ${2}"
}

if [[ "${BASH_SOURCE[0]}" == "${0}" ]]; then
  runTheThingWithParameters "$@"
fi
```

Note the proper argument passing with `"$@"` which allows for arguments that contain whitespace and such.

```bash
$ ./yourscript.sh hello world
hello world
$ source yourscript.sh
$ runTheThing hello bash
hello bash
$
```

#### Run binaries and logic code only inside functions

Environment variables and constants are okay, but once logic runs outside a function it will be executed during script sourcing.

#### Source with (dynamic yet fixed-up) environment variables

Shellcheck basically says this is a no-no. Anyhow unless the test container allows for  appropriate script paths there is hardly a way around it:

```bash
sourcingExitCode=0
# shellcheck disable=SC1090
source "${STARTUP_DIR}"/util.sh || sourcingExitCode=$?
if [[ ${sourcingExitCode} -ne 0 ]]; then
  echo "ERROR: An error occurred while sourcing /util.sh."
fi
```

At least make sure that the variables are properly set into the production (f. i. `Dockerfile`)and test environment (set-up an env var in your test).
