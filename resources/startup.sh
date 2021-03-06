#!/bin/bash
set -o errexit
set -o nounset
set -o pipefail

# shellcheck disable=SC1091
source /etc/ces/functions.sh
LDAP_BIND_PASSWORD="$(/opt/apache-tomcat/webapps/usermgt/WEB-INF/cipher.sh encrypt "$(doguctl config -e sa-ldap/password)" | tail -1)"
export LDAP_BIND_PASSWORD

PASSWORD_POLICY="password_policy"
OPTIONAL_CONFIG_PATH="/var/lib/usermgt/conf/optional.conf"


# copy resources
if [ ! -d "/var/lib/usermgt/conf" ]; then
	mkdir -p /var/lib/usermgt/conf
fi

cp -rf /resources/* /var/lib/usermgt/conf/

# create log directory
if [ ! -d "/var/lib/usermgt/logs" ]; then
	mkdir -p /var/lib/usermgt/logs
	chown -R tomcat:tomcat /var/lib/usermgt/logs
fi

# render templates
doguctl template "/var/lib/usermgt/conf/cas.xml.tpl" "/var/lib/usermgt/conf/cas.xml"
doguctl template "/var/lib/usermgt/conf/ldap.xml.tpl" "/var/lib/usermgt/conf/ldap.xml"

# create password policy config file
echo "Read password policy"
POLICY="$(doguctl config "${PASSWORD_POLICY}" --default '{ "Rules": [] }')"
echo "Password policy is: ${POLICY}"
echo "${POLICY}" > "${OPTIONAL_CONFIG_PATH}"

# create truststore, which is used in the setenv.sh
create_truststore.sh > /dev/null

# wait until ldap passed all health checks
echo "wait until ldap passes all health checks"
if ! doguctl healthy --wait --timeout 120 ldap; then
  echo "timeout reached by waiting of ldap to get healthy"
  exit 1
fi

# start tomcat as user tomcat
su - tomcat -c "exec /opt/apache-tomcat/bin/catalina.sh run"

