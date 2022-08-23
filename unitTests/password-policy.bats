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

@test "the minimum password length should be 14 if this is configured in the etcd" {
   source /workspace/resources/util.sh

   mock_set_output "${doguctl}" "14"

   run determinePwdMinLength

   assert_equal "$(mock_get_call_num "${doguctl}")" "1"
   assert_line "The minimum password length is 14"
}

@test "create password policy pattern should create a correct regex when no rule is activated" {
   source /workspace/resources/util.sh

   mock_set_output "${doguctl}" "0" 1

   run determinePwdMinLength

   assert_equal "$(mock_get_call_num "${doguctl}")" "1"
   assert_line "Warning: Password minimum length is configured as less than 1; password minimum length is set to 1"
   assert_line "The minimum password length is 1"
}

@test "create password policy pattern should create a correct regex when incorrect values are stored in the etcd" {
   source /workspace/resources/util.sh

   mock_set_output "${doguctl}" "Hundert" 1

   run determinePwdMinLength

   assert_equal "$(mock_get_call_num "${doguctl}")" "1"
   assert_line "Warning: Specified minimum length is not an integer; password minimum length is set to 1"
   assert_line "The minimum password length is 1"
}