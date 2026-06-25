ARG TOMCAT_MAJOR_VERSION=9
ARG TOMCAT_VERSION=9.0.117
ARG TOMCAT_TARGZ_SHA512=82b15278a7bfa2685c80e07963c43246df4fd742d574b608a68f5ce67c6ffde0eff3e224cc9809925cc6bf7002a190c3bf420f50c0e4052467d3e665efc84a54

FROM eclipse-temurin:8-jdk AS builder

WORKDIR /usermgt

COPY app/pom.xml pom.xml
COPY app/mvnw mvnw
COPY app/.mvn .mvn

RUN ./mvnw dependency:go-offline -B
COPY app/ .
RUN ./mvnw package -DskipTests -B

FROM alpine:3.23 AS binaryConcentrator
ARG TOMCAT_VERSION
ARG TOMCAT_MAJOR_VERSION
ARG TOMCAT_TARGZ_SHA512

RUN apk add --no-cache wget unzip

# Download and extract Tomcat
RUN wget -q -O /tmp/tomcat.tar.gz "http://archive.apache.org/dist/tomcat/tomcat-${TOMCAT_MAJOR_VERSION}/v${TOMCAT_VERSION}/bin/apache-tomcat-${TOMCAT_VERSION}.tar.gz" \
    && echo "${TOMCAT_TARGZ_SHA512}  /tmp/tomcat.tar.gz" | sha512sum -c - \
    && mkdir -p /opt/apache-tomcat \
    && tar -xzf /tmp/tomcat.tar.gz -C /opt/apache-tomcat --strip-components=1 \
    && rm -rf /tmp/tomcat.tar.gz /opt/apache-tomcat/webapps/*

# Usermgt
WORKDIR /opt/apache-tomcat/webapps/usermgt
COPY --from=builder /usermgt/target/usermgt-*.war ./usermgt.war
RUN unzip usermgt.war \
    && rm usermgt.war \
    && chmod +x WEB-INF/cipher.sh

FROM registry.cloudogu.com/official/java:8.482.08-2

ARG TOMCAT_VERSION

LABEL NAME="official/usermgt" \
   VERSION="1.21.0-4" \
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

# dependencies xmlstarlet and openldap-clients are required for givenname migration
RUN set -eux && \
    apk update && apk upgrade && \
    addgroup -S -g 1000 tomcat && \
    adduser -S -h /opt/apache-tomcat -s /bin/bash -G tomcat -u 1000 tomcat && \
    apk add --no-cache xmlstarlet openldap-clients && \
    rm -rf /var/cache/apk/*

COPY --chown=1000:1000 --from=binaryConcentrator /opt/apache-tomcat /opt/apache-tomcat
COPY --chown=1000:1000 resources /

USER 1000:1000
EXPOSE 8080

HEALTHCHECK --interval=5s CMD doguctl healthy usermgt || exit 1

CMD /startup.sh
