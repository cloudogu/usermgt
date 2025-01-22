ARG TOMCAT_MAJOR_VERSION=9
ARG TOMCAT_VERSION=9.0.96
ARG TOMCAT_TARGZ_SHA512=ef3ac81debbc3a519c43d1fdb1c88ab26a8052af424d81bceccfbd6e663050a06d7aad7960fd5d11c17849829daebbebf33d92ac1158902283d0e534514aab93

FROM timbru31/java-node:8-jdk-18 as builder

VOLUME "${USER_HOME_DIR}/.m2"

WORKDIR /usermgt

COPY app/pom.xml pom.xml
COPY app/mvnw mvnw
COPY app/.mvn .mvn

RUN ./mvnw dependency:resolve-plugins dependency:resolve
COPY app/ .
RUN ./mvnw package -DskipTests


FROM registry.cloudogu.com/official/base:3.21.0-1 as tomcat

ARG TOMCAT_MAJOR_VERSION
ARG TOMCAT_VERSION
ARG TOMCAT_TARGZ_SHA512

ENV TOMCAT_MAJOR_VERSION=${TOMCAT_MAJOR_VERSION} \
    TOMCAT_VERSION=${TOMCAT_VERSION} \
    TOMCAT_TARGZ_SHA512=${TOMCAT_TARGZ_SHA512}

RUN set -eux && \
  apk update && \
  apk add wget

RUN set -eux && \
  wget -O  "apache-tomcat-${TOMCAT_VERSION}.tar.gz" \
  "http://archive.apache.org/dist/tomcat/tomcat-${TOMCAT_MAJOR_VERSION}/v${TOMCAT_VERSION}/bin/apache-tomcat-${TOMCAT_VERSION}.tar.gz" \
  && echo "${TOMCAT_TARGZ_SHA512} *apache-tomcat-${TOMCAT_VERSION}.tar.gz" | sha512sum -c - \
  && gunzip "apache-tomcat-${TOMCAT_VERSION}.tar.gz" \
  && tar xf "apache-tomcat-${TOMCAT_VERSION}.tar" -C /opt \
  && rm "apache-tomcat-${TOMCAT_VERSION}.tar"


FROM registry.cloudogu.com/official/java:8u402-6 AS binaryConcentrator
# Prepare all file system actions here to achieve a simpler dogu build below.
# Also this allows the developers to act on a finer granularity when it comes to file system changes
ARG TOMCAT_VERSION

RUN set -eux \
    && addgroup -S -g 1000 tomcat \
    && adduser -S -h /opt/apache-tomcat -s /bin/bash -G tomcat -u 1000 tomcat

# Tomcat
COPY --chown=1000:1000 --from=tomcat /opt/apache-tomcat-${TOMCAT_VERSION} /opt/apache-tomcat/
# reduce attack surface and remove unnecessary default webapps
RUN set -eux \
  && rm -rf /opt/apache-tomcat/webapps/*

# Usermgt
WORKDIR /opt/apache-tomcat/webapps/usermgt
COPY --chown=1000:1000 --from=builder /usermgt/target/usermgt-*.war .

RUN set -eux \
    && ls -lha /opt/apache-tomcat/webapps/usermgt \
    && mv usermgt-*.war usermgt.war \
    && unzip usermgt.war \
    && rm -f usermgt.war \
    && chmod +x WEB-INF/cipher.sh

# clean up permissions
RUN chown -R tomcat:tomcat /opt/apache-tomcat


FROM registry.cloudogu.com/official/java:8u432-1

ARG TOMCAT_VERSION

LABEL NAME="official/usermgt" \
   VERSION="1.18.0-1" \
   maintainer="hello@cloudogu.com"

# mark as webapp for nginx
ENV SERVICE_TAGS=webapp \
    # tomcat version
    TOMCAT_VERSION=${TOMCAT_VERSION} \
    # home of the app configuration
    UNIVERSEADM_HOME=/var/lib/usermgt/conf2 \
    TRUSTSTORE=/opt/apache-tomcat/truststore.jks \
    CATALINA_SH=/opt/apache-tomcat/bin/catalina.sh \
    STARTUP_DIR=/

RUN set -eux \
    apk update && \
    apk upgrade && \
    addgroup -S -g 1000 tomcat && \
    adduser -S -h /opt/apache-tomcat -s /bin/bash -G tomcat -u 1000 tomcat && \
# install dependencies for givenname migration
    apk add --no-cache \
          xmlstarlet \
          openldap-clients && \
    rm -rf /var/cache/apk/*

COPY --chown=1000:1000 --from=binaryConcentrator /opt/apache-tomcat /opt/apache-tomcat
COPY --chown=1000:1000 resources /

USER 1000:1000
EXPOSE 8080

HEALTHCHECK --interval=5s CMD doguctl healthy usermgt || exit 1

CMD /startup.sh
