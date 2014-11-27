universeadm
===========

1. Requirements
 - Oracle JDK >= 7
 - Apache Maven >= 3
 - Vagrant >= 1.6
 - Windows: VirtualBox
 - Linux: Docker

2. Build project
 - mvn clean install

3. start development environment (requires vagrant)
 - vagrant up

4. Start application for development (port 8084)
 - mvn package jetty:run

5. Fast development application start (port 8084)
 - mvn -DskipTests -P'!webcomponents' package jetty:run

6. Start application with release configuration (port 8084)
 - mvn package jetty:run-war

CAS Account
username: admin
password: admin
