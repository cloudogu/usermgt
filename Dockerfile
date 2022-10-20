FROM openjdk:8u302-jdk as builder
COPY app/ /usermgt
RUN set -x \
 && cd /usermgt \
 && ./mvnw package


FROM registry.cloudogu.com/official/java:8u302-3

LABEL NAME="official/usermgt" \
   VERSION="1.8.1-1" \
   maintainer="hello@cloudogu.com"

# mark as webapp for nginx
ENV SERVICE_TAGS=webapp \
    # tomcat version
    TOMCAT_MAJOR_VERSION=8 \
    TOMCAT_VERSION=8.5.73 \
    TOMCAT_TARGZ_SHA256=f8965400c9f21361ff81ff04478dbb4ce365276d14b0b99b85912c9de949f6a0 \
    # home directory
    UNIVERSEADM_HOME=/var/lib/usermgt/conf

COPY --from=builder /usermgt/target/usermgt-*.war /usermgt.war

# create user
RUN set -o errexit \
    && apk update \
    && apk upgrade \
    && addgroup -S -g 1000 tomcat \
    && adduser -S -h /opt/apache-tomcat -s /bin/bash -G tomcat -u 1000 tomcat \
    # install tomcat
    && wget -O  "apache-tomcat-${TOMCAT_VERSION}.tar.gz" \
    "http://archive.apache.org/dist/tomcat/tomcat-${TOMCAT_MAJOR_VERSION}/v${TOMCAT_VERSION}/bin/apache-tomcat-${TOMCAT_VERSION}.tar.gz" \
    && echo "${TOMCAT_TARGZ_SHA256} *apache-tomcat-${TOMCAT_VERSION}.tar.gz" | sha256sum -c - \
    && gunzip "apache-tomcat-${TOMCAT_VERSION}.tar.gz" \
    && tar xf "apache-tomcat-${TOMCAT_VERSION}.tar" -C /opt \
    && rm "apache-tomcat-${TOMCAT_VERSION}.tar" \
    && mv "/opt/apache-tomcat-${TOMCAT_VERSION}/"* /opt/apache-tomcat \
    && rmdir  "/opt/apache-tomcat-${TOMCAT_VERSION}" \
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

# healtcheck
HEALTHCHECK CMD doguctl healthy usermgt || exit 1

# execution
CMD /startup.sh
