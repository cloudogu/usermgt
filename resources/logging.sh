#!/bin/bash
set -o errexit
set -o nounset
set -o pipefail

# logging behaviour can be configured in logging/root with the following options <ERROR,WARN,INFO,DEBUG>
DEFAULT_LOGGING_KEY="logging/root"
LOG_LEVEL_ERROR="ERROR"
LOG_LEVEL_WARN="WARN"
LOG_LEVEL_INFO="INFO"
LOG_LEVEL_DEBUG="DEBUG"
export CATALINA_LOGLEVEL=""
export DEFAULT_SLEEP_IN_SECS_BEFORE_ERROR=120

# list of accepted log levels
DEFAULT_LOG_LEVEL=${LOG_LEVEL_WARN}

# logging configuration used to configure the apache-tomcat logging mechanism
TOMCAT_LOGGING="/opt/apache-tomcat/conf/logging.properties"
TOMCAT_LOGGING_TEMPLATE="${TOMCAT_LOGGING}.tpl"

# create a mapping because apache uses different log levels than log4j eg. ERROR=>SEVERE
function mapDoguLogLevel() {
  echo "Mapping dogu specific log level"
  local currentLogLevel
  currentLogLevel=$(doguctl config --default "${DEFAULT_LOG_LEVEL}" "${DEFAULT_LOGGING_KEY}")

  echo "Mapping ${currentLogLevel} to Catalina"
  case "${currentLogLevel}" in
  "${LOG_LEVEL_ERROR}")
    CATALINA_LOGLEVEL="SEVERE"
    export CATALINA_LOGLEVEL
    ;;
  "${LOG_LEVEL_INFO}")
    echo "found INFO"
    CATALINA_LOGLEVEL="INFO"
    export CATALINA_LOGLEVEL
    ;;
  "${LOG_LEVEL_DEBUG}")
    export CATALINA_LOGLEVEL="FINE"
    ;;
  *)
    echo "Falling back to WARNING"
    export CATALINA_LOGLEVEL="WARNING"
    ;;
  esac

  echo "Log level mapping ended successfully..."
}

function validateDoguLogLevel() {
  echo "Validate root log level"

  local validateExitCode=0
  doguctl validate "${DEFAULT_LOGGING_KEY}" || validateExitCode=$?

  if [[ ${validateExitCode} -ne 0 ]]; then
    echo "WARNING: The loglevel configured in ${DEFAULT_LOGGING_KEY} is invalid."
    echo "WARNING: Removing misconfigured value."
    doguctl config --rm "${DEFAULT_LOGGING_KEY}"
  fi

  return
}

renderLoggingProperties() {
  echo "Rendering logging configuration..."

  local templatingSuccessful=0
  doguctl template ${TOMCAT_LOGGING_TEMPLATE} ${TOMCAT_LOGGING} || templatingSuccessful=$?

  if [[ ${templatingSuccessful} -ne 0 ]]; then
  doguctl state "LoggingTemplateError"
  echo "Could not template ${TOMCAT_LOGGING_TEMPLATE} file."
  sleep ${DEFAULT_SLEEP_IN_SECS_BEFORE_ERROR}
  exit 2
  fi
}

renderLoggingFiles() {
  echo "Starting log level mapping..."

  validateDoguLogLevel
  mapDoguLogLevel
  renderLoggingProperties
}
