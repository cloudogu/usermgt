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
OPTIONAL_CONFIG_PATH="/var/lib/usermgt/conf/optional.conf"
GUI_CONFIG_PATH="/var/lib/usermgt/conf/gui.conf"
CIPHER_SH="/opt/apache-tomcat/webapps/usermgt/WEB-INF/cipher.sh"

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

waitForPostUpgrade() {
  # check whether post-upgrade script is still running
while [[ "$(doguctl state)" == "upgrading" ]]; do
  echo "Upgrade script is running. Waiting..."
  sleep 3
done
}

encryptLdapPassword() {
  LDAP_BIND_PASSWORD="$(${CIPHER_SH} encrypt "$(doguctl config -e sa-ldap/password)" | tail -1)"
  export LDAP_BIND_PASSWORD
}

copyResources() {
  if [ ! -d "/var/lib/usermgt/conf" ]; then
    mkdir -p /var/lib/usermgt/conf
  fi

  cp -rf /resources/* /var/lib/usermgt/conf/
}

buildMailAddress() {
  GLOBAL_MAIL_ADDRESS="$(doguctl config --global --default "info@cloudogu.com" mail_address)"
  MAIL_ADDRESS="$(doguctl config --default "${GLOBAL_MAIL_ADDRESS}" mail_address)"
  export MAIL_ADDRESS
}

renderTemplates() {
  determinePwdMinLength

  doguctl template "/var/lib/usermgt/conf/cas.xml.tpl" "/var/lib/usermgt/conf/cas.xml"
  doguctl template "/var/lib/usermgt/conf/ldap.xml.tpl" "/var/lib/usermgt/conf/ldap.xml"
  doguctl template "/var/lib/usermgt/conf/application-configuration.xml.tpl" "/var/lib/usermgt/conf/application-configuration.xml"
  doguctl template "/var/lib/usermgt/conf/password_policy.tpl" "${OPTIONAL_CONFIG_PATH}"

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

  waitForPostUpgrade
  encryptLdapPassword
  copyResources
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
