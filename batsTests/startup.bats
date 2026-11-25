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
  export LDAP_BIND_PASSWORD_ENC=""
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
  assert_equal "${LDAP_BIND_PASSWORD_ENC}" "YmFzZTY0IGlzIG5vdCBlbmNyeXB0aW9u"
}

@test "setMfaEnv() should pass global 2FA settings and encrypted Dogu credentials to the Java process" {
  export UNIVERSEADM_HOME=""
  export CATALINA_OPTS="-Xmx256m"
  source /workspace/resources/startup.sh
  mock_set_status "${doguctl}" 0
  mock_set_output "${doguctl}" "mfa-user" 1
  mock_set_output "${doguctl}" "mfa-password" 2
  mock_set_output "${doguctl}" "ces.example.com" 3

  setMfaEnv true

  assert_equal "$(mock_get_call_args "${doguctl}" "1")" "config --encrypted 2fa/api_user_name"
  assert_equal "$(mock_get_call_args "${doguctl}" "2")" "config --encrypted 2fa/api_user_password"
  assert_equal "$(mock_get_call_args "${doguctl}" "3")" "config -g fqdn"
  assert_equal "${CATALINA_OPTS}" "-Xmx256m -Dcas.mfa.activate=true -Dcas.mfa.user=mfa-user -Dcas.mfa.password=mfa-password -Dcas.mfa.fqdn=ces.example.com"
}

@test "createGuiConfiguration() should publish the MFA activation flag as a JSON boolean" {
  export UNIVERSEADM_HOME="${BATS_TEST_TMPDIR}"
  export EXTERNAL_LDAP=false
  source /workspace/resources/startup.sh
  mock_set_status "${doguctl}" 0
  mock_set_output "${doguctl}" "false"
  mock_set_output "${doguctl}" "true" 1
  local mfaActivated

  createGuiConfiguration mfaActivated
  assert_equal "$(mock_get_call_args "${doguctl}" "1")" "config --global --default false 2fa/activate"
  assert_equal "${mfaActivated}" "true"
  assert_equal "$(cat "${GUI_CONFIG_PATH}")" '{"pwdResetPreselected": false, "externalLdap": false, "mfaActive": true}'

  createGuiConfiguration mfaActivated
  assert_equal "${mfaActivated}" "false"
  assert_equal "$(cat "${GUI_CONFIG_PATH}")" '{"pwdResetPreselected": false, "externalLdap": false, "mfaActive": false}'
}
