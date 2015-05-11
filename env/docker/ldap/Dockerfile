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

ADD resources /resources

ADD startup.sh /startup.sh
RUN chmod +x /startup.sh

EXPOSE 389

VOLUME [ "/data" ]

CMD ["/startup.sh"]
