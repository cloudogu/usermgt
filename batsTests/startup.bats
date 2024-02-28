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
  cipher="$(mock_create)"
  export cipher
  ln -s "${cipher}" "${BATS_TMPDIR}/cipher.sh"
  export PATH="${PATH}:${BATS_TMPDIR}"
}

teardown() {
  unset STARTUP_DIR
  rm "${BATS_TMPDIR}/doguctl"
  rm "${BATS_TMPDIR}/cipher.sh"
}

@test "encryptLdapPassword() should set env var with LDAP password" {
  export UNIVERSEADM_HOME=""
  source /workspace/resources/startup.sh
  export LDAP_BIND_PASSWORD=""
  export CIPHER_SH=${cipher}
  mock_set_status "${doguctl}" 0
  mock_set_output "${doguctl}" "Password1!"
  mock_set_status "${cipher}" 0
  mock_set_output "${cipher}" "YmFzZTY0IGlzIG5vdCBlbmNyeXB0aW9u"

  encryptLdapPassword

  assert_success
  assert_equal "$(mock_get_call_num "${doguctl}")" "1"
  assert_equal "$(mock_get_call_args "${doguctl}" "1")" "config -e sa-ldap/password"
  assert_equal "$(mock_get_call_num "${cipher}")" "1"
  assert_equal "$(mock_get_call_args "${cipher}" "1")" "encrypt Password1!"
  assert_equal "${LDAP_BIND_PASSWORD}" "YmFzZTY0IGlzIG5vdCBlbmNyeXB0aW9u"
}
