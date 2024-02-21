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

@test "mapDoguLogLevel() should set log level to INFO if INFO was configured" {
  source /workspace/resources/logging.sh
  export CATALINA_LOGLEVEL=""
  mock_set_status "${doguctl}" 0
  mock_set_output "${doguctl}" "INFO" 1

  mapDoguLogLevel

  assert_success
  assert_equal "$(mock_get_call_num "${doguctl}")" "1"
  assert_equal "$(mock_get_call_args "${doguctl}" "1")" "config --default WARN logging/root"
  assert_equal "${CATALINA_LOGLEVEL}" "INFO"
}
@test "mapDoguLogLevel() should set log level to SEVERE if ERROR was configured" {
  source /workspace/resources/logging.sh
  export CATALINA_LOGLEVEL=""
  mock_set_status "${doguctl}" 0
  mock_set_output "${doguctl}" "ERROR" 1

  mapDoguLogLevel

  assert_success
  assert_equal "$(mock_get_call_num "${doguctl}")" "1"
  assert_equal "$(mock_get_call_args "${doguctl}" "1")" "config --default WARN logging/root"
  assert_equal "${CATALINA_LOGLEVEL}" "SEVERE"
}
@test "mapDoguLogLevel() should set log level to FINE if DEBUG was configured" {
  source /workspace/resources/logging.sh
  export CATALINA_LOGLEVEL=""
  mock_set_status "${doguctl}" 0
  mock_set_output "${doguctl}" "DEBUG" 1

  mapDoguLogLevel

  assert_success
  assert_equal "$(mock_get_call_num "${doguctl}")" "1"
  assert_equal "$(mock_get_call_args "${doguctl}" "1")" "config --default WARN logging/root"
  assert_equal "${CATALINA_LOGLEVEL}" "FINE"
}
@test "mapDoguLogLevel() should set log level to WARNING if WARN was configured" {
  source /workspace/resources/logging.sh
  export CATALINA_LOGLEVEL=""
  mock_set_status "${doguctl}" 0
  mock_set_output "${doguctl}" "WARN" 1

  mapDoguLogLevel

  assert_success
  assert_equal "$(mock_get_call_num "${doguctl}")" "1"
  assert_equal "$(mock_get_call_args "${doguctl}" "1")" "config --default WARN logging/root"
  assert_equal "${CATALINA_LOGLEVEL}" "WARNING"
}
@test "mapDoguLogLevel() should set log level to WARNING if anything invalid was configured" {
  source /workspace/resources/logging.sh
  export CATALINA_LOGLEVEL=""
  mock_set_status "${doguctl}" 0
  mock_set_output "${doguctl}" "roflmao" 1

  mapDoguLogLevel

  assert_success
  assert_equal "$(mock_get_call_num "${doguctl}")" "1"
  assert_equal "$(mock_get_call_args "${doguctl}" "1")" "config --default WARN logging/root"
  assert_equal "${CATALINA_LOGLEVEL}" "WARNING"
}

@test "validateDoguLogLevel() should return on valid log levels" {
  source /workspace/resources/logging.sh
  mock_set_status "${doguctl}" 0

  run validateDoguLogLevel

  assert_success
  assert_equal "$(mock_get_call_num "${doguctl}")" "1"
  assert_equal "$(mock_get_call_args "${doguctl}" "1")" "validate logging/root"
}
@test "validateDoguLogLevel() should fail on invalid log levels, warn about it, and remove bad config key" {
  source /workspace/resources/logging.sh
  mock_set_status "${doguctl}" 42

  run validateDoguLogLevel

  assert_failure
  assert_equal "$(mock_get_call_num "${doguctl}")" "2"
  assert_equal "$(mock_get_call_args "${doguctl}" "1")" "validate logging/root"
  assert_equal "$(mock_get_call_args "${doguctl}" "2")" "config --rm logging/root"
  assert_line "WARNING: The loglevel configured in logging/root is invalid."
  assert_line "WARNING: Removing misconfigured value."
}

@test "renderLoggingProperties() should call doguctl to render logging.properties" {
  source /workspace/resources/logging.sh
  mock_set_status "${doguctl}" 0

  run renderLoggingProperties

  assert_success
  assert_equal "$(mock_get_call_num "${doguctl}")" "1"
  assert_equal "$(mock_get_call_args "${doguctl}" "1")" "template /opt/apache-tomcat/conf/logging.properties.tpl /opt/apache-tomcat/conf/logging.properties"
}
@test "renderLoggingProperties() should fail on template error" {
  source /workspace/resources/logging.sh
  export DEFAULT_SLEEP_IN_SECS_BEFORE_ERROR=1
  mock_set_status "${doguctl}" 1

  run renderLoggingProperties

  assert_failure
  assert_equal "$(mock_get_call_num "${doguctl}")" "2"
  assert_equal "$(mock_get_call_args "${doguctl}" "1")" "template /opt/apache-tomcat/conf/logging.properties.tpl /opt/apache-tomcat/conf/logging.properties"
  assert_equal "$(mock_get_call_args "${doguctl}" "2")" "state LoggingTemplateError"
  assert_line "Could not template /opt/apache-tomcat/conf/logging.properties.tpl file."
}
