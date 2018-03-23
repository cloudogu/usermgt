#!/bin/bash
sed "s/%LDAPSERVER%/${LDAP_PORT_389_TCP_ADDR}:${LDAP_PORT_389_TCP_PORT}/g" /root/cas.properties > /var/lib/tomcat7/webapps/cas/WEB-INF/cas.properties

source /etc/default/tomcat7
export JAVA_HOME="/usr/lib/jvm/java-7-oracle"
export CATALINA_HOME="/usr/share/tomcat7"
export CATALINA_BASE="/var/lib/tomcat7"
export CATALINA_PID="/var/run/tomcat7.pid"
export CATALINA_TMPDIR="/tmp/tomcat7"

mkdir /tmp/tomcat7
chown tomcat7 /tmp/tomcat7
cd /tmp/tomcat7
su tomcat7 /usr/share/tomcat7/bin/catalina.sh run
