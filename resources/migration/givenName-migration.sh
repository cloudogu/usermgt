#!/bin/bash

set -o errexit

# Path to the LDAP configuration file
xml_file="/var/lib/usermgt/conf/ldap.xml"

echo
echo "##### Start migration for givenName #####"
echo

echo "Reading ldap information from ${xml_file}"

# Extract LDAP configuration information using xmlstarlet
LDAP_HOST=$(xmlstarlet sel -t -v "//ldap/host" "${xml_file}")
LDAP_PORT=$(xmlstarlet sel -t -v "//ldap/port" "${xml_file}")
BIND_DN=$(xmlstarlet sel -t -v "//ldap/bind-dn" "${xml_file}")
USER_BASE_DN=$(xmlstarlet sel -t -v "//ldap/user-base-dn" "${xml_file}")

# Retrieve LDAP bind password from doguctl
BIND_PASSWORD=$(doguctl config -e sa-ldap/password)

# Default value for the givenName attribute
DEFAULT_GIVEN_NAME="Unknown"

LDAP_SERVER="ldap://${LDAP_HOST}:${LDAP_PORT}"

# Search filter for users without a given name
SEARCH_FILTER="(&(objectClass=person)(!(givenName=*)))"

# Retrieve DNs of users without a given name
USER_DNS=$(ldapsearch -x -H "${LDAP_SERVER}" -b "${USER_BASE_DN}" -D "${BIND_DN}" -w "${BIND_PASSWORD}" -LLL "${SEARCH_FILTER}" dn | awk '/^dn: / {print $2}')

# Iterate through user DNs and update the givenName attribute
for USER_DN in ${USER_DNS}; do
    echo "Updating ${USER_DN}"
    ldapmodify -x -H "${LDAP_SERVER}" -D "${BIND_DN}" -w "${BIND_PASSWORD}" <<EOF
dn: ${USER_DN}
changetype: modify
replace: givenName
givenName: ${DEFAULT_GIVEN_NAME}
EOF
done

echo
echo "##### Migration finished successfully #####"
echo