# Local development

The user management interface can be started locally.

## Step 1: Install Dependencies
```
yarn global add json-server
```

## Step 2: Start (mock) backend
```
cd app/src/main/ui
yarn backend
```

## Step 3: Start frontend
```
cd app/src/main/ui
yarn install
yarn dev
```
## With Dogu backend running inside a local CES instance

### Apply patch file

```shell
git apply docs/development/local_development_settings.patch
```

### Rebuild Usermgt inside the CES

```shell
cesapp build /vagrant/containers/usermgt
```

These changed must not be committed! They are meant to help with local development. It's easier to
use a real backend with all its dependencies already installed.

## Insert generated test data

- Create users: `create_users.py <user count>`

Executing the script without arguments will create 5 users by default. The scripts counter always starts
with 0. The script will not interrupt its execution if a data conflict occurred.

Example: create 10 users
```shell
docs/development/create_users.py 10
```

- Create groups: `create_groups.py <Gruppenanzahl>`

Executing the script without arguments will create 5 groups by default. The scripts counter always starts
with 0. The script will not interrupt its execution if a data conflict occurred.

Example: create 10 groups
```shell
docs/development/create_groups.py 10
```
