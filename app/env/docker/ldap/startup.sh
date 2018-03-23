#!/bin/bash

LOGLEVEL=256
CONFDIR=/etc/cesldap

# LDAP ALREADY INITIALIZED?
if [ ! -f "$CONFDIR/ldap.conf"  ]; then
  mv /etc/ldap/* "$CONFDIR"
  rmdir /etc/ldap
  ln -s "$CONFDIR" /etc/ldap

  # get domain and root password
  LDAP_ROOTPASS="trio123"
  LDAP_BASE_DOMAIN="cloudogu.com"
  LDAP_DOMAIN="ces.local"
  ADMIN_USERNAME="admin"
  # TODO change admin password
  ADMIN_PASSWORD="$(slappasswd -s admin)"
  SYSTEM_USERNAME="system"
  # TODO change system password
  SYSTEM_PASSWORD="$(slappasswd -s system)"

	# set domain and root password
	cat <<EOF | debconf-set-selections
slapd slapd/internal/generated_adminpw password ${LDAP_ROOTPASS}
slapd slapd/internal/adminpw password ${LDAP_ROOTPASS}
slapd slapd/password2 password ${LDAP_ROOTPASS}
slapd slapd/password1 password ${LDAP_ROOTPASS}
slapd slapd/dump_database_destdir string /var/backups/slapd-VERSION
slapd slapd/domain string ${LDAP_BASE_DOMAIN}
slapd shared/organization string ${LDAP_BASE_DOMAIN}
slapd slapd/backend string HDB
slapd slapd/purge_database boolean true
slapd slapd/move_old_database boolean true
slapd slapd/allow_ldap_v2 boolean false
slapd slapd/no_configuration boolean false
slapd slapd/dump_database select when needed
EOF

  # reconfigure slapd
  dpkg-reconfigure -f noninteractive slapd

	# start ldap
	/usr/sbin/slapd -h "ldap:/// ldapi:///" -4 -u openldap -g openldap -d $LOGLEVEL &
  sleep 2

  # enable memberOf
  ldapadd -Q -Y EXTERNAL -H ldapi:/// -f /resources/memberof-overlay.ldif
  ldapmodify -Q -Y EXTERNAL -H ldapi:/// -f /resources/refint.ldif

  # index attributes
  ldapmodify -Q -Y EXTERNAL -H ldapi:/// -f /resources/index.ldif

  # restart
  kill -INT $!
  sleep 1
  /usr/sbin/slapd -h "ldap:/// ldapi:///" -4 -u openldap -g openldap -d $LOGLEVEL &
  sleep 2

  # add structure
  ldapadd -D"cn=admin,dc=cloudogu,dc=com" -x -w"${LDAP_ROOTPASS}" -f "/resources/domain.ldif"
  # bring slapd back to foreground
  wait
else
  # START LDAP
  /usr/sbin/slapd -h "ldap:///" -4 -u openldap -g openldap -d $LOGLEVEL
fi