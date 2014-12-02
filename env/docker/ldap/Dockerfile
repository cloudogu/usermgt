# Dockerfile to build a ldap server for DEVELOPMENT #
# None of the following is meant for production, esp. from a security pov #

# Inspired from
# https://github.com/Enalean/docker-ldap-dev

## Use the official docker ubuntu distribution ##
FROM debian:wheezy

## Get some karma ##
MAINTAINER Sebastian Sdorra <sebastian.sdorra@triology.de>

# install slapd
RUN apt-get update -qq && apt-get install -y supervisor slapd ldap-utils -qq && apt-get clean

# Default password is trio123
RUN sed -i 's/^olcRootPW:.*$/olcRootPW: {SSHA}rH06nxWDFjgcSZdK7VJyb9VPRHNzl0qR/g' /etc/ldap/slapd.d/cn\=config/olcDatabase\=\{1\}hdb.ldif
RUN sed -i 's/cn=admin,dc=nodomain/cn=Directory Manager,dc=triology,dc=de/g' /etc/ldap/slapd.d/cn\=config/olcDatabase\=\{1\}hdb.ldif
RUN sed -i 's/dc=nodomain/dc=triology,dc=de/g' /etc/ldap/slapd.d/cn\=config/olcDatabase\=\{1\}hdb.ldif

ADD sample.ldif /root/sample.ldif
ADD user.ldif /root/user.ldif

RUN service slapd start;\
    sleep 3;\
    ldapadd -f /root/sample.ldif -D "cn=Directory Manager,dc=triology,dc=de" -w trio123;\
    ldapadd -f /root/user.ldif -D "cn=Directory Manager,dc=triology,dc=de" -w trio123;\
    service slapd stop

ADD supervisord.conf /etc/supervisord.conf

ADD run.sh /root/run.sh
RUN chmod +x /root/run.sh

EXPOSE 389

VOLUME [ "/data" ]

CMD ["/root/run.sh"]
