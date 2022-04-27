### Setup local ldap without CES

#### Software Requirements
* Following prerequisites have to be met:
    - Install Oracle JDK / Open JDK 8
    - Install Maven (check with mvn -version if jdk 8 is correctly setup / change JAVA_HOME if not)
    - Install Docker

#### Setup local LDAP using Docker
* Checkout the following repository https://github.com/cloudogu/docker-sample-ldap
* build the container `docker build -t usermgt/ldap .`
* run the container `docker run --rm -p 389:389 usermgt/ldap`

#### Setup Usermgt Development Mode
* `export UNIVERSEADM_STAGE=DEVELOPMENT`

#### Build project:
- `./mvnw clean install`

#### Build the project and start the server
* `mvn -DskipTests -P'!webcomponents' package jetty:run-war `

#### Open the application
* `http://localhost:8084/universeadm/`
- Use Base Authentication `User: admin | Password: admin`

### Setup local LDAP using CES
* Bind the ldap port to the host system (e.g https://stackoverflow.com/questions/19335444/how-do-i-assign-a-port-mapping-to-an-existing-docker-container)
    - alternative way add ExposedPorts to the `dogu.json` and rebuild the container
* Change the `ldap.xml` configuration make sure the passwort is ciphered.
    - easy solution jump inside the usermgt container and copy the `ldap.xml`
    - alternative solution use the `cipher.sh` inside the usermgt container ` /opt/apache-tomcat/webapps/usermgt/WEB-INF/cipher.sh encrypt <PASSWORD>`


