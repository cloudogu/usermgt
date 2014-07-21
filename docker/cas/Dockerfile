# Dockerfile to build a ldap server for DEVELOPMENT #
# None of the following is meant for production, esp. from a security pov #

# inspired by https://registry.hub.docker.com/u/wnameless/cas-mysql/dockerfile

## Use the official docker ubuntu distribution ##
FROM ubuntu:12.04

## Get some karma ##
MAINTAINER Sebastian Sdorra <sebastian.sdorra@triology.de>

# install wget, java, tomcat, supervisor
RUN echo "deb http://ppa.launchpad.net/webupd8team/java/ubuntu trusty main"\
  > /etc/apt/sources.list.d/webupd8team-java.list
RUN echo "deb-src http://ppa.launchpad.net/webupd8team/java/ubuntu trusty main"\
  >> /etc/apt/sources.list.d/webupd8team-java.list

RUN apt-key adv --keyserver keyserver.ubuntu.com --recv-keys EEA14886
RUN apt-get update -y -qq && apt-get install -y wget supervisor oracle-java7-installer tomcat7 -qq
RUN apt-get clean && rm -rf /var/lib/apt/lists/* /tmp/* /var/tmp/*

# configure https
RUN sed -i "s#</Server>##g" /etc/tomcat7/server.xml; \
    sed -i "s#  </Service>##g" /etc/tomcat7/server.xml; \
    echo '    <Connector port="8443" protocol="HTTP/1.1" SSLEnabled="true" maxThreads="150" scheme="https" secure="true" clientAuth="false" sslProtocol="TLS" keystoreFile="/usr/share/tomcat7/.keystore" keystorePass="tomcat_admin" />' >> /etc/tomcat7/server.xml; \
    echo '  </Service>' >> /etc/tomcat7/server.xml; \
    echo '</Server>' >> /etc/tomcat7/server.xml

# install cas
RUN cd /tmp; \
    wget http://downloads.jasig.org/cas/cas-server-3.5.2-release.tar.gz; \
    tar xzvf cas-server-3.5.2-release.tar.gz; \
    mkdir /var/lib/tomcat7/webapps/cas;
    unzip -d /var/lib/tomcat7/webapps/cas cas-server-3.5.2/modules/cas-server-webapp-3.5.2.war; \
    cp cas-server-3.5.2/modules/cas-server-support-ldap-3.5.2.jar /var/lib/tomcat7/webapps/cas/WEB-INF/lib

ADD
ADD run.sh /root/run.sh
ADD supervisord.conf /etc/supervisord.conf

EXPOSE 389

VOLUME [ "/data" ]

CMD ["/root/run.sh"]
