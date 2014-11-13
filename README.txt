universeadm
===========

1. Requirements
 - Oracle JDK >= 7
 - Apache Maven >= 3

2. Build project
 - mvn clean install

3. Build development environment (requires docker)
 - docker build -t 'scmmu/ldap' env/docker/ldap
 - docker build -t 'scmmu/cas' env/docker/cas

4. First start of development environment (requires docker)
 - ./env/env.sh run (binds ldap to 1389 and cas to 8443)

5. Start development environment
 - ./env/env.sh start

6. Start application for development (port 8084)
 - mvn package jetty:run

7. Fast development application start (port 8084)
 - mvn -DskipTests -P'!webcomponents' package jetty:run

8. Start application with release configuration (port 8084)
 - mvn package jetty:run-war

CAS Account
username: admin
password: admin
