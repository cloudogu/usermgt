FROM openjdk:8u151-jdk as builder
COPY app/ /usermgt
RUN set -x \
 && cd /usermgt \
 && ./mvnw package


FROM registry.cloudogu.com/official/java:8u151-2
MAINTAINER Sebastian Sdorra <sebastian.sdorra@cloudogu.com>
# mark as webapp for nginx
ENV SERVICE_TAGS=webapp \
    # tomcat version
    TOMCAT_MAJOR_VERSION=8 \
    TOMCAT_VERSION=8.0.45 \
    # home directory
    UNIVERSEADM_HOME=/var/lib/usermgt/conf

COPY --from=builder /usermgt/target/usermgt-*.war /usermgt.war

# create user
RUN set -x \
    && addgroup -S -g 1000 tomcat \
    && mkdir /opt \
    && adduser -S -h /opt/apache-tomcat -s /bin/bash -G tomcat -u 1000 tomcat \
    # install tomcat
    && curl --fail --location --retry 3 \
    http://archive.apache.org/dist/tomcat/tomcat-${TOMCAT_MAJOR_VERSION}/v${TOMCAT_VERSION}/bin/apache-tomcat-${TOMCAT_VERSION}.tar.gz \
    | gunzip \
    | tar x -C /opt \
    && mv /opt/apache-tomcat-${TOMCAT_VERSION}/* /opt/apache-tomcat \
    && chown -R tomcat:tomcat /opt/apache-tomcat \
    && rm -rf /opt/apache-tomcat/logs \
    && mkdir /var/lib/usermgt \
    && ln -s /var/lib/usermgt/logs /opt/apache-tomcat/logs \
    && rm -rf /opt/apache-tomcat/webapps/* \
    # install usermgt
    && mkdir -p /opt/apache-tomcat/webapps/usermgt \
    && cd /opt/apache-tomcat/webapps/usermgt \
    && mv /usermgt.war . \
    && unzip usermgt.war \
    && rm -f usermgt.war \
    && chmod +x WEB-INF/cipher.sh \
    # fix permissions
    && chown -R tomcat:tomcat /opt/apache-tomcat

# copy required files
COPY resources /

# expose port
EXPOSE 8080

# execution
CMD /startup.sh
