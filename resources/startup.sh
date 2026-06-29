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
  local raw_pass=""

  if [[ -n "${LDAP_BIND_PASSWORD:-}" ]]; then
    raw_pass="${LDAP_BIND_PASSWORD}"
  elif [[ "${EXTERNAL_LDAP:-false}" == "true" ]]; then
    raw_pass=$(doguctl config -e ldap/bind_password)
  else
    raw_pass=$(doguctl config -e sa-ldap/password)
  fi

  LDAP_BIND_PASSWORD_ENC="$(${CIPHER_SH} encrypt "${raw_pass}" | tail -1)"
  export LDAP_BIND_PASSWORD_ENC

  echo "Encrypted ldap password..."
}

setLdapUser() {
  if [[ -n "${LDAP_BIND_USER:-}" ]]; then
    return
  elif [[ "${EXTERNAL_LDAP:-false}" == "true" ]]; then
    LDAP_BIND_USER=$(doguctl config ldap/bind_user)
  else
    LDAP_BIND_USER=$(doguctl config -e sa-ldap/username)
  fi

  export LDAP_BIND_USER
  echo "Set ldap user..."
}

configureLDAP() {
  # host
  if [[ -z "${LDAP_HOST:-}" ]]; then
    LDAP_HOST=$(doguctl config ldap/host --default "ldap")
    export LDAP_HOST
  fi

  # port
  if [[ -z "${LDAP_PORT:-}" ]]; then
    LDAP_PORT=$(doguctl config ldap/port --default "389")
    export LDAP_PORT
  fi

# user base dn
  if [[ -z "${LDAP_USER_BASE_DN:-}" ]]; then
    LDAP_USER_BASE_DN=""

    if user_base_dn="$(doguctl config ldap/user_base_dn 2>/dev/null)"; then
      if [[ -n "${user_base_dn}" ]]; then
        LDAP_USER_BASE_DN="${user_base_dn}"
        export LDAP_USER_BASE_DN
      fi
    fi
  fi

  # group base dn
  if [[ -z "${LDAP_GROUP_BASE_DN:-}" ]]; then
    LDAP_GROUP_BASE_DN=""

    if group_base_dn="$(doguctl config ldap/group_base_dn 2>/dev/null)"; then
      if [[ -n "${group_base_dn}" ]]; then
        LDAP_GROUP_BASE_DN="${group_base_dn}"
        export LDAP_GROUP_BASE_DN
      fi
    fi
  fi

  # Set EXTERNAL_LDAP to true if LDAP_HOST is not "ldap" or "lop-idp-ldap"
  if [[ "${LDAP_HOST}" != "ldap" && "${LDAP_HOST}" != "lop-idp-ldap" ]]; then
    EXTERNAL_LDAP="true"
  else
    EXTERNAL_LDAP="false"
  fi
  export EXTERNAL_LDAP

  encryptLdapPassword
  setLdapUser

  echo "LDAP_HOST: ${LDAP_HOST}"
  echo "Configured ldap..."
}

copyConfigurationResources() {
  mkdir -p "${UNIVERSEADM_HOME}"

  cp -rf "${APP_CONFIG_RESOURCE_SRC}"/* "${UNIVERSEADM_HOME}"
}

renderTemplates() {
  echo "Running renderTemplates"
  determinePwdMinLength

  echo "determined pwd min length..."

  doguctl template "${UNIVERSEADM_HOME}/cas.xml.tpl" "${UNIVERSEADM_HOME}/cas.xml"
  echo "rendered cas config..."



  doguctl template "${UNIVERSEADM_HOME}/ldap.xml.tpl" "${UNIVERSEADM_HOME}/ldap.xml"
  echo "rendered ldap config..."
  doguctl template "${UNIVERSEADM_HOME}/application-configuration.xml.tpl" "${UNIVERSEADM_HOME}/application-configuration.xml"
  echo "rendered app config..."
  doguctl template "${UNIVERSEADM_HOME}/mail.xml.tpl" "${UNIVERSEADM_HOME}/mail.xml"
  echo "rendered mail config..."
  doguctl template "${UNIVERSEADM_HOME}/password_policy.tpl" "${OPTIONAL_CONFIG_PATH}"
  echo "rendered pwd policy config..."

  renderLoggingFiles
}

createGuiConfiguration() {
  echo "Read configuration of preselection of password reset attribute checkbox"
  PWD_RESET_PRESELECTION="$(doguctl config "${PASSWORD_RESET_DEFAULT_VALUE_KEY}" --default 'false')"
  echo "Preselection of password reset attribute checkbox is: ${PWD_RESET_PRESELECTION}"

  echo "{ \"pwdResetPreselected\": ${PWD_RESET_PRESELECTION}, \"externalLdap\": ${EXTERNAL_LDAP}}" >"${GUI_CONFIG_PATH}"
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

# these env are used by the frontend to connect to the cas mfa api
setMfaEnv() {
      local mfaApiUser=""
      local mfaApiPassword=""

      if ! mfaApiUser="$(doguctl config experimental/totp/api_user_name 2>/dev/null)"; then
        echo "Skipping MFA env setup because experimental/totp/api_user_name is not configured"
        return
      fi

      if ! mfaApiPassword="$(doguctl config experimental/totp/api_user_password 2>/dev/null)"; then
        echo "Skipping MFA env setup because experimental/totp/api_user_password is not configured"
        return
      fi

      if [[ -z "${mfaApiUser}" || -z "${mfaApiPassword}" ]]; then
        echo "Skipping MFA env setup because TOTP API config is empty"
        return
      fi

      FQDN=$(doguctl config -g fqdn)

      # Set Java system properties for backend
      export CATALINA_OPTS="${CATALINA_OPTS:-} -Dcas.mfa.user=${mfaApiUser}"
      export CATALINA_OPTS="${CATALINA_OPTS} -Dcas.mfa.password=${mfaApiPassword}"
      export CATALINA_OPTS="${CATALINA_OPTS} -Dcas.mfa.fqdn=${FQDN}"
}

startTomcat() {
  "${CATALINA_SH}" run
}

runMain() {
  printCloudoguLogo
  configureLDAP
  copyConfigurationResources
  renderTemplates
  createGuiConfiguration
  createTrustStore
  setMfaEnv

  if [[ "${EXTERNAL_LDAP}" != "true" ]]; then
    if [[ -z ${COMPONENT:-} ]]; then
      waitForLDAPDogu
    fi
    migrateLDAPEntries
  else
    echo "Skipping LDAP migration because external LDAP is configured"
  fi

  startTomcat
}

# make the script only run when executed, not when sourced from bats tests)
if [[ "${BASH_SOURCE[0]}" == "${0}" ]]; then
  runMain
fi
