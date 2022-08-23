#! /bin/bash
# Bind an unbound BATS variables that fail all tests when combined with 'set -o nounset'
export BATS_TEST_START_TIME="0"
export BATSLIB_FILE_PATH_REM=""
export BATSLIB_FILE_PATH_ADD=""

load '/workspace/target/bats_libs/bats-support/load.bash'
load '/workspace/target/bats_libs/bats-assert/load.bash'
load '/workspace/target/bats_libs/bats-mock/load.bash'
load '/workspace/target/bats_libs/bats-file/load.bash'

setup() {
  export STARTUP_DIR=/workspace/resources
  export WORKDIR=/workspace

  doguctl="$(mock_create)"

  export PATH="${PATH}:${BATS_TMPDIR}"
  ln -s "${doguctl}" "${BATS_TMPDIR}/doguctl"
}

teardown() {
  unset doguctl
  rm "${BATS_TMPDIR}/doguctl"
}

@test "check same version with two equal version numbers" {
  source /workspace/resources/post-upgrade.sh

  mock_set_output "${doguctl}" "empty" 1

  run checkSameVersion "1.0.0" "1.0.0"

  assert_success
  assert_equal "$(mock_get_call_num "${doguctl}")" "1"
  assert_line "Checking the Usermgt versions..."
  assert_line "FROM and TO versions are the same"
}

@test "check same version with two different version numbers" {
  source /workspace/resources/post-upgrade.sh

  mock_set_output "${doguctl}" "empty" 1

  run checkSameVersion "1.0.0" "2.0.1"

  assert_success
  assert_equal "$(mock_get_call_num "${doguctl}")" "0"
  assert_line "Checking the Usermgt versions..."
  assert_line "Checking the Usermgt versions... Done!"
}

@test "remove deprecated etcd keys that exist" {
  source /workspace/resources/post-upgrade.sh

  mock_set_output "${doguctl}" "password-policy-rules" 1

  run removeDeprecatedKeys

  assert_success
  assert_equal "$(mock_get_call_num "${doguctl}")" "2"
  assert_line "Remove deprecated etcd Keys..."
  assert_line "Notice: The password policy is now no longer held within the Dogu. Instead, global etcd keys are now used."
  assert_line "The password policy is NOT migrated. Old etcd key will be removed."
  assert_line "Remove deprecated etcd Keys... Done!"
}

@test "remove deprecated etcd does nothing, if keys do not exist" {
  source /workspace/resources/post-upgrade.sh

  mock_set_output "${doguctl}" "empty" 1

  run removeDeprecatedKeys

  assert_success
  assert_equal "$(mock_get_call_num "${doguctl}")" "1"
  assert_line "Remove deprecated etcd Keys... Done!"
}