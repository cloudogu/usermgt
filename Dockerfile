ARG TOMCAT_MAJOR_VERSION=8
ARG TOMCAT_VERSION=8.5.88
ARG TOMCAT_TARGZ_SHA512=c31c794092b160c5b0099f4dfb5cf17d711d93ae68a60e414691dba65ad80c78a5fb602c7010d1226dae424b83921e440bd858b3eb0ef90b7932316d3ab44c1f

FROM timbru31/java-node:8-jdk-18 as builder
COPY app/pom.xml /usermgt/pom.xml
COPY app/mvnw /usermgt/mvnw
COPY app/.mvn /usermgt/.mvn
RUN set -x \
    && cd /usermgt \
    && ./mvnw dependency:resolve-plugins dependency:resolve

COPY app/ /usermgt
RUN set -x \
     && cd /usermgt \
     && ./mvnw package

FROM registry.cloudogu.com/official/base:3.17.3-2 as tomcat

ARG TOMCAT_MAJOR_VERSION
ARG TOMCAT_VERSION
ARG TOMCAT_TARGZ_SHA512

ENV TOMCAT_MAJOR_VERSION=${TOMCAT_MAJOR_VERSION} \
    TOMCAT_VERSION=${TOMCAT_VERSION} \
    TOMCAT_TARGZ_SHA512=${TOMCAT_TARGZ_SHA512}

RUN apk update && apk add wget && wget -O  "apache-tomcat-${TOMCAT_VERSION}.tar.gz" \
  "http://archive.apache.org/dist/tomcat/tomcat-${TOMCAT_MAJOR_VERSION}/v${TOMCAT_VERSION}/bin/apache-tomcat-${TOMCAT_VERSION}.tar.gz" \
  && echo "${TOMCAT_TARGZ_SHA512} *apache-tomcat-${TOMCAT_VERSION}.tar.gz" | sha512sum -c - \
  && gunzip "apache-tomcat-${TOMCAT_VERSION}.tar.gz" \
  && tar xf "apache-tomcat-${TOMCAT_VERSION}.tar" -C /opt \
  && rm "apache-tomcat-${TOMCAT_VERSION}.tar"


FROM registry.cloudogu.com/official/java:8u372-1

ARG TOMCAT_VERSION

LABEL NAME="official/usermgt" \
   VERSION="1.12.0-1" \
   maintainer="hello@cloudogu.com"

# mark as webapp for nginx
ENV SERVICE_TAGS=webapp \
    # tomcat version
    TOMCAT_VERSION=${TOMCAT_VERSION} \
    # home directory
    UNIVERSEADM_HOME=/var/lib/usermgt/conf

COPY --from=builder /usermgt/target/usermgt-*.war /usermgt.war

# create user
RUN set -o errexit \
    && apk update \
    && apk upgrade \
    && addgroup -S -g 1000 tomcat \
    && adduser -S -h /opt/apache-tomcat -s /bin/bash -G tomcat -u 1000 tomcat

#install tomcat
COPY --from=tomcat /opt/apache-tomcat-${TOMCAT_VERSION} /opt/apache-tomcat

RUN chown -R tomcat:tomcat /opt/apache-tomcat \
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

# install dependencies for givenname migration 
RUN apk add --no-cache \
    xmlstarlet \
    openldap-clients \
    && rm -rf /var/cache/apk/*

# copy required files
COPY resources /

# expose port
EXPOSE 8080

# healtcheck
HEALTHCHECK --interval=5s CMD doguctl healthy usermgt || exit 1

# execution
CMD /startup.sh
