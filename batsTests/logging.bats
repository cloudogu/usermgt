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

@test "mapDoguLogLevels() should set log level to INFO if INFO was configured" {
  source /workspace/resources/logging.sh
  export CATALINA_LOGLEVEL=""
  export LOGBACK_LEVEL=""
  mock_set_status "${doguctl}" 0
  mock_set_output "${doguctl}" "INFO" 1

  mapDoguLogLevels

  assert_success
  assert_equal "$(mock_get_call_num "${doguctl}")" "1"
  assert_equal "$(mock_get_call_args "${doguctl}" "1")" "config --default WARN logging/root"
  assert_equal "${CATALINA_LOGLEVEL}" "INFO"
  assert_equal "${LOGBACK_LEVEL}" "INFO"
}
@test "mapDoguLogLevels() should set log level to SEVERE if ERROR was configured" {
  source /workspace/resources/logging.sh
  export CATALINA_LOGLEVEL=""
  export LOGBACK_LEVEL=""
  mock_set_status "${doguctl}" 0
  mock_set_output "${doguctl}" "ERROR" 1

  mapDoguLogLevels

  assert_success
  assert_equal "$(mock_get_call_num "${doguctl}")" "1"
  assert_equal "$(mock_get_call_args "${doguctl}" "1")" "config --default WARN logging/root"
  assert_equal "${CATALINA_LOGLEVEL}" "SEVERE"
  assert_equal "${LOGBACK_LEVEL}" "ERROR"
}
@test "mapDoguLogLevels() should set log level to FINE if DEBUG was configured" {
  source /workspace/resources/logging.sh
  export CATALINA_LOGLEVEL=""
  export LOGBACK_LEVEL=""
  mock_set_status "${doguctl}" 0
  mock_set_output "${doguctl}" "DEBUG" 1

  mapDoguLogLevels

  assert_success
  assert_equal "$(mock_get_call_num "${doguctl}")" "1"
  assert_equal "$(mock_get_call_args "${doguctl}" "1")" "config --default WARN logging/root"
  assert_equal "${CATALINA_LOGLEVEL}" "FINE"
  assert_equal "${LOGBACK_LEVEL}" "TRACE"
}
@test "mapDoguLogLevels() should set log level to WARNING if WARN was configured" {
  source /workspace/resources/logging.sh
  export CATALINA_LOGLEVEL=""
  export LOGBACK_LEVEL=""
  mock_set_status "${doguctl}" 0
  mock_set_output "${doguctl}" "WARN" 1

  mapDoguLogLevels

  assert_success
  assert_equal "$(mock_get_call_num "${doguctl}")" "1"
  assert_equal "$(mock_get_call_args "${doguctl}" "1")" "config --default WARN logging/root"
  assert_equal "${CATALINA_LOGLEVEL}" "WARNING"
  assert_equal "${LOGBACK_LEVEL}" "WARN"
}
@test "mapDoguLogLevels() should set log level to WARNING if anything invalid was configured" {
  source /workspace/resources/logging.sh
  export CATALINA_LOGLEVEL=""
  export LOGBACK_LEVEL=""
  mock_set_status "${doguctl}" 0
  mock_set_output "${doguctl}" "roflmao" 1

  mapDoguLogLevels

  assert_success
  assert_equal "$(mock_get_call_num "${doguctl}")" "1"
  assert_equal "$(mock_get_call_args "${doguctl}" "1")" "config --default WARN logging/root"
  assert_equal "${CATALINA_LOGLEVEL}" "WARNING"
  assert_equal "${LOGBACK_LEVEL}" "WARN"
}

@test "validateDoguLogLevel() should return on valid log levels" {
  source /workspace/resources/logging.sh
  mock_set_status "${doguctl}" 0

  run validateDoguLogLevel

  assert_success
  assert_equal "$(mock_get_call_num "${doguctl}")" "1"
  assert_equal "$(mock_get_call_args "${doguctl}" "1")" "validate logging/root"
}
@test "validateDoguLogLevel() should fail on invalid log levels, print an error message and exit with code 1" {
  source /workspace/resources/logging.sh
  mock_set_status "${doguctl}" 42

  run validateDoguLogLevel

  assert_failure
  assert_equal "$(mock_get_call_num "${doguctl}")" "1"
  assert_equal "$(mock_get_call_args "${doguctl}" "1")" "validate logging/root"
  assert_line "ERROR: The loglevel configured in logging/root is invalid."
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
  assert_equal "$(mock_get_call_num "${doguctl}")" "1"
  assert_equal "$(mock_get_call_args "${doguctl}" "1")" "template /opt/apache-tomcat/conf/logging.properties.tpl /opt/apache-tomcat/conf/logging.properties"
  assert_line "Could not template log /opt/apache-tomcat/conf/logging.properties.tpl to path /opt/apache-tomcat/conf/logging.properties: exited with 1"
}
@test "renderLogbackXml() should fail on template error" {
  source /workspace/resources/logging.sh
  export DEFAULT_SLEEP_IN_SECS_BEFORE_ERROR=1
  mock_set_status "${doguctl}" 1

  run renderLogbackXml

  assert_failure
  assert_equal "$(mock_get_call_num "${doguctl}")" "1"
  assert_equal "$(mock_get_call_args "${doguctl}" "1")" "template /opt/apache-tomcat/conf/logback.xml.tpl /opt/apache-tomcat/webapps/usermgt/WEB-INF/classes/logback.xml"
  assert_line "Could not template log /opt/apache-tomcat/conf/logback.xml.tpl to path /opt/apache-tomcat/webapps/usermgt/WEB-INF/classes/logback.xml: exited with 1"
}
