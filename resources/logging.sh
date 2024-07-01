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
LOGBACK_XML_TEMPLATE="/opt/apache-tomcat/conf/logback.xml.tpl"
LOGBACK_XML="/opt/apache-tomcat/webapps/usermgt/WEB-INF/classes/logback.xml"

# create a mapping because different logging frameworks use different log levels, eg. ERROR=>SEVERE
function mapDoguLogLevels() {
  echo "Mapping dogu specific log level"
  local currentLogLevel
  currentLogLevel=$(doguctl config --default "${DEFAULT_LOG_LEVEL}" "${DEFAULT_LOGGING_KEY}")


  echo "Mapping ${currentLogLevel} to Catalina"
  case "${currentLogLevel}" in
  "${LOG_LEVEL_ERROR}")
    export CATALINA_LOGLEVEL="SEVERE"
    export LOGBACK_LEVEL="ERROR"
    ;;
  "${LOG_LEVEL_INFO}")
    export CATALINA_LOGLEVEL="INFO"
    export LOGBACK_LEVEL="INFO"
    ;;
  "${LOG_LEVEL_DEBUG}")
    export CATALINA_LOGLEVEL="FINE"
    export LOGBACK_LEVEL="TRACE"
    ;;
  *)
    echo "Falling back to WARNING"
    export CATALINA_LOGLEVEL="WARNING"
    export LOGBACK_LEVEL="WARN"
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
  renderLoggingFile "${TOMCAT_LOGGING_TEMPLATE}" "${TOMCAT_LOGGING}"
}

renderLogbackXml() {
  renderLoggingFile "${LOGBACK_XML_TEMPLATE}" "${LOGBACK_XML}"
}

renderLoggingFile() {
  local template="${1}"
  local targetFile="${2}"
  echo "Rendering logging configuration ${targetFile}..."

  local templatingSuccessful=0
  doguctl template ${template} ${targetFile} || templatingSuccessful=$?

  if [[ ${templatingSuccessful} -ne 0 ]]; then
    echo "Could not template log ${template} to path ${targetFile}: exited with ${templatingSuccessful}"
    sleep ${DEFAULT_SLEEP_IN_SECS_BEFORE_ERROR}
    exit 2
  fi
}

renderLoggingFiles() {
  echo "Starting log level mapping..."

  validateDoguLogLevel
  mapDoguLogLevels
  renderLoggingProperties
  renderLogbackXml
}
