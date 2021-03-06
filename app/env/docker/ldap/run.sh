#!/bin/bash

if [ ! -d /data/ldap ]; then
    mv /var/lib/ldap /data
    chown -R openldap:openldap /data
else 
    rm -rf /var/lib/ldap
fi

ln -s /data/ldap /var/lib/ldap
exec /usr/bin/supervisord -n