#!/bin/bash
set -o errexit
set -o nounset
set -o pipefail

sourcingExitCode=0
# shellcheck disable=SC1090,SC1091
source "${STARTUP_DIR}"/util.sh || sourcingExitCode=$?
if [[ ${sourcingExitCode} -ne 0 ]]; then
  echo "ERROR: An error occurred while sourcing ${STARTUP_DIR}/util.sh."
fi

# shellcheck disable=SC1090,SC1091
source "${STARTUP_DIR}"/logging.sh || sourcingExitCode=$?
if [[ ${sourcingExitCode} -ne 0 ]]; then
  echo "ERROR: An error occurred while sourcing ${STARTUP_DIR}/logging.sh."
fi

PASSWORD_RESET_DEFAULT_VALUE_KEY="pwd_reset_selected_by_default"
OPTIONAL_CONFIG_PATH="${UNIVERSEADM_HOME}/optional.conf"
GUI_CONFIG_PATH="${UNIVERSEADM_HOME}/gui.conf"
CIPHER_SH="/opt/apache-tomcat/webapps/usermgt/WEB-INF/cipher.sh"
APP_CONFIG_RESOURCE_SRC=/resources

printCloudoguLogo() {
  echo "                                     ./////,                    "
  echo "                                 ./////==//////*                "
  echo "                                ////.  ___   ////.              "
  echo "                         ,**,. ////  ,////A,  */// ,**,.        "
  echo "                    ,/////////////*  */////*  *////////////A    "
  echo "                   ////'        \VA.   '|'   .///'       '///*  "
  echo "                  *///  .*///*,         |         .*//*,   ///* "
  echo "                  (///  (//////)**--_./////_----*//////)   ///) "
  echo "                   V///   '°°°°      (/////)      °°°°'   ////  "
  echo "                    V/////(////////\. '°°°' ./////////(///(/'   "
  echo "                       'V/(/////////////////////////////V'      "
}

encryptLdapPassword() {
  LDAP_BIND_PASSWORD="$(${CIPHER_SH} encrypt "$(doguctl config -e sa-ldap/password)" | tail -1)"
  export LDAP_BIND_PASSWORD
}

copyConfigurationResources() {
  mkdir -p "${UNIVERSEADM_HOME}"

  cp -rf "${APP_CONFIG_RESOURCE_SRC}"/* "${UNIVERSEADM_HOME}"
}

buildMailAddress() {
  GLOBAL_MAIL_ADDRESS="$(doguctl config --global --default "info@cloudogu.com" mail_address)"
  MAIL_ADDRESS="$(doguctl config --default "${GLOBAL_MAIL_ADDRESS}" mail_address)"
  export MAIL_ADDRESS
}

renderTemplates() {
  determinePwdMinLength

  doguctl template "${UNIVERSEADM_HOME}/cas.xml.tpl" "${UNIVERSEADM_HOME}/cas.xml"
  doguctl template "${UNIVERSEADM_HOME}/ldap.xml.tpl" "${UNIVERSEADM_HOME}/ldap.xml"
  doguctl template "${UNIVERSEADM_HOME}/application-configuration.xml.tpl" "${UNIVERSEADM_HOME}/application-configuration.xml"
  doguctl template "${UNIVERSEADM_HOME}/mail.xml.tpl" "${UNIVERSEADM_HOME}/mail.xml"
  doguctl template "${UNIVERSEADM_HOME}/password_policy.tpl" "${OPTIONAL_CONFIG_PATH}"

  renderLoggingFiles
}

createGuiConfiguration() {
  echo "Read configuration fof preselection of password reset attribute checkbox"
  PWD_RESET_PRESELECTION="$(doguctl config "${PASSWORD_RESET_DEFAULT_VALUE_KEY}" --default 'false')"
  echo "Preselection of password reset attribute checkbox is: ${PWD_RESET_PRESELECTION}"
  echo "{ \"pwdResetPreselected\": ${PWD_RESET_PRESELECTION}}" >"${GUI_CONFIG_PATH}"
}

createTrustStore() {
  # this is used in the setenv.sh
  echo "creating truststore"
  local exitCode=0
  output=$(create_truststore.sh "${TRUSTSTORE}") || exitCode=$?
  if [[ ${exitCode} -ne 0 ]]; then
    echo "Error creating truststore: Exit code ${exitCode}: ${output}"
    exit ${exitCode}
  fi
}

waitForLDAPDogu() {
  # wait until ldap passed all health checks
  echo "wait until ldap passes all health checks"
  if ! doguctl healthy --wait --timeout 120 ldap; then
    echo "timeout reached by waiting of ldap to get healthy"
    exit 1
  fi
}

migrateLDAPEntries() {
  # migrate entries with missing givenName attribute
  # shellcheck disable=SC1091
  source /migration/givenName-migration.sh
}

startTomcat() {
  "${CATALINA_SH}" run
}

runMain() {
  printCloudoguLogo

  encryptLdapPassword
  copyConfigurationResources
  buildMailAddress
  renderTemplates
  createGuiConfiguration
  createTrustStore

  waitForLDAPDogu
  migrateLDAPEntries

  startTomcat
}

# make the script only run when executed, not when sourced from bats tests)
if [[ "${BASH_SOURCE[0]}" == "${0}" ]]; then
  runMain
fi
