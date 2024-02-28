#!/bin/bash
set -o errexit
set -o nounset
set -o pipefail

function checkSameVersion() {
  FROM_VERSION="${1}"
  TO_VERSION="${2}"

  echo "Checking the Usermgt versions..."
  if [ "${FROM_VERSION}" = "${TO_VERSION}" ]; then
    echo "FROM and TO versions are the same"
    echo "Set registry flag so startup script can start afterwards..."
    doguctl state "upgrade done"
    echo "Exiting..."
    exit 0
  fi
  echo "Checking the Usermgt versions... Done!"
}

function removeDeprecatedKeys() {
  echo "Remove deprecated etcd Keys..."
  if [ "$(doguctl config "password_policy" -d "empty")" != "empty" ]; then
    echo
    echo "Notice: The password policy is now no longer held within the Dogu. Instead, global etcd keys are now used."
    echo "The password policy is NOT migrated. Old etcd key will be removed."
    echo
    doguctl config --remove "password_policy"
  fi

  echo "Remove deprecated etcd Keys... Done!"
}

function run_postupgrade() {
  FROM_VERSION="${1}"
  TO_VERSION="${2}"

  echo "Executing Usermgt post-upgrade from ${FROM_VERSION} to ${TO_VERSION} ..."

  checkSameVersion ${FROM_VERSION} ${TO_VERSION}
  removeDeprecatedKeys

  echo "Set registry flag so startup script can start afterwards..."
  doguctl state "upgrade done"

  echo "Executing Usermgt post-upgrade from ${FROM_VERSION} to ${TO_VERSION} ... Done!"
}

# make the script only run when executed, not when sourced from bats tests)
if [[ "${BASH_SOURCE[0]}" == "${0}" ]]; then
  run_postupgrade "$@"
fi
