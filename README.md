![Cloudogu logo](https://cloudogu.com/images/logo.png)

[![GitHub license](https://img.shields.io/github/license/cloudogu/usermgt.svg)](https://github.com/cloudogu/usermgt/blob/develop/LICENSE)
[![GitHub release](https://img.shields.io/github/release/cloudogu/usermgt.svg)](https://github.com/cloudogu/usermgt/releases)

# User Management Dogu
https://cloudogu.com

This repository contains the usermgt, an maven project written in java.

### Software Requirements
* Following prerequisites have to be met:
  - Install Oracle JDK / Open JDK 8
  - Install Maven (check with mvn -version if jdk 8 is correctly setup / change JAVA_HOME if not)
  - Install Docker

### Setup local LDAP using Docker  
* Checkout the following repository https://github.com/cloudogu/docker-sample-ldap  
* build the container `docker build -t usermgt/ldap .`
* run the container `docker run -rm -p389:389 usermgt/ldap`

### Setup local LDAP using CES
* Bind the ldap port to the host system (e.g https://stackoverflow.com/questions/19335444/how-do-i-assign-a-port-mapping-to-an-existing-docker-container)
  - alternative way add ExposedPorts to the `dogu.json` and rebuild the container
* Change the `ldap.xml` configuration make sure the passwort is ciphered.
  - easy solution jump inside the usermgt container and copy the `ldap.xml`
  - alternative solution use the `cipher.sh` inside the usermgt container ` /opt/apache-tomcat/webapps/usermgt/WEB-INF/cipher.sh encrypt <PASSWORD>`

### Setup Usermgt Development Mode
* `export UNIVERSEADM_STAGE=DEVELOPMENT`

### Build the project and start the development server
* `mvn -DskipTests -P'!webcomponents' package jetty:run-war `

* Build project:
  - `./mvnw clean install`
* Start application for development (Port 8084) with `mvn package jetty:run-war`
* Fast development application start (port 8084) with `mvn -DskipTests -P'!webcomponents' package jetty:run-war`
* Start application with release configuration (port 8084) with `mvn package jetty:run-war`
* Use CAS Account
  - username: admin
  - password: admin  

---
### What is Cloudogu?
Cloudogu is an open platform, which lets you choose how and where your team creates great software. Each service or tool is delivered as a [Dōgu](https://translate.google.com/?text=D%26%23x014d%3Bgu#ja/en/%E9%81%93%E5%85%B7), a Docker container, that can be easily integrated in your environment just by pulling it from our registry. We have a growing number of ready-to-use Dōgus, e.g. SCM-Manager, Jenkins, Nexus, SonarQube, Redmine and many more. Every Dōgu can be tailored to your specific needs. You can even bring along your own Dōgus! Take advantage of a central authentication service, a dynamic navigation, that lets you easily switch between the web UIs and a smart configuration magic, which automatically detects and responds to dependencies between Dōgus. Cloudogu is open source and it runs either on-premise or in the cloud. Cloudogu is developed by Cloudogu GmbH under [MIT License](https://cloudogu.com/license.html) and it runs either on-premise or in the cloud.

### How to get in touch?
Want to talk to the Cloudogu team? Need help or support? There are several ways to get in touch with us:

* [Website](https://cloudogu.com)
* [Mailing list](https://groups.google.com/forum/#!forum/cloudogu)
* [Email hello@cloudogu.com](mailto:hello@cloudogu.com)

---
&copy; 2016 Cloudogu GmbH - MADE WITH :heart: FOR DEV ADDICTS. [Legal notice / Impressum](https://cloudogu.com/imprint.html)
