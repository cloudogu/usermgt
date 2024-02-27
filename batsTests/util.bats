#! /bin/bash
# Bind an unbound BATS variables that fail all tests when combined with 'set -o nounset'
export BATS_TEST_START_TIME="0"
export BATSLIB_FILE_PATH_REM=""
export BATSLIB_FILE_PATH_ADD=""
export output=""
export status=""

load '/workspace/target/bats_libs/bats-support/load.bash'
load '/workspace/target/bats_libs/bats-assert/load.bash'
load '/workspace/target/bats_libs/bats-mock/load.bash'
load '/workspace/target/bats_libs/bats-file/load.bash'

setup() {
  export STARTUP_DIR=/workspace/resources/
  doguctl="$(mock_create)"
  export doguctl
  ln -s "${doguctl}" "${BATS_TMPDIR}/doguctl"
  export PATH="${PATH}:${BATS_TMPDIR}"
}

teardown() {
  unset STARTUP_DIR
  rm "${BATS_TMPDIR}/doguctl"
}

@test "determinePwdMinLength() should set env var with configured password minimum length" {
  source /workspace/resources/util.sh
  export PWD_MIN_LENGTH=""
  mock_set_status "${doguctl}" 0
  mock_set_output "${doguctl}" "42"

  determinePwdMinLength

  assert_success
  assert_equal "$(mock_get_call_num "${doguctl}")" "1"
  assert_equal "$(mock_get_call_args "${doguctl}" "1")" "config -default 1 --global password-policy/min_length"
  assert_equal "${PWD_MIN_LENGTH}" "42"
}
@test "determinePwdMinLength() should default to env var value 1 with invalid password length value" {
  source /workspace/resources/util.sh
  export PWD_MIN_LENGTH=""
  mock_set_status "${doguctl}" 0
  mock_set_output "${doguctl}" "NaN"

  determinePwdMinLength

  assert_success
  assert_equal "$(mock_get_call_num "${doguctl}")" "1"
  assert_equal "$(mock_get_call_args "${doguctl}" "1")" "config -default 1 --global password-policy/min_length"
  assert_equal "${PWD_MIN_LENGTH}" "1"
}
@test "determinePwdMinLength() should default to env var value 1 with password value less than 1" {
  source /workspace/resources/util.sh
  export PWD_MIN_LENGTH=""
  mock_set_status "${doguctl}" 0
  mock_set_output "${doguctl}" "0"

  determinePwdMinLength

  assert_success
  assert_equal "$(mock_get_call_num "${doguctl}")" "1"
  assert_equal "$(mock_get_call_args "${doguctl}" "1")" "config -default 1 --global password-policy/min_length"
  assert_equal "${PWD_MIN_LENGTH}" "1"
}
