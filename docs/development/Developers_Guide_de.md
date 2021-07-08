### Software-Anforderungen
* Die folgenden Voraussetzungen müssen erfüllt sein:
    - Oracle JDK / Open JDK 8 installieren
    - Maven installieren (mit mvn -version prüfen, ob jdk 8 korrekt eingerichtet ist / falls nicht, JAVA_HOME ändern)
    - Docker installieren

### Lokales LDAP mit Docker einrichten
* Checken Sie das folgende Repository aus https://github.com/cloudogu/docker-sample-ldap
* Bauen Sie den Container `docker build -t usermgt/ldap .`
* Starten Sie den Container `docker run --rm -p389:389 usermgt/ldap`.

### Lokales LDAP mit CES einrichten
* Binden Sie den ldap-Port an das Host-System (z.B. https://stackoverflow.com/questions/19335444/how-do-i-assign-a-port-mapping-to-an-existing-docker-container)
    - alternativ ExposedPorts zur `dogu.json` hinzufügen und den Container neu aufbauen
* Ändern Sie die `ldap.xml`-Konfiguration und stellen Sie sicher, dass das Passwort verschlüsselt ist.
    - einfache Lösung springen Sie in den usermgt-Container und kopieren Sie die `ldap.xml`
    - alternative Lösung verwenden Sie die `cipher.sh` innerhalb des usermgt-Containers ` /opt/apache-tomcat/webapps/usermgt/WEB-INF/cipher.sh encrypt <PASSWORD>`

### Usermgt Entwicklungsmodus einrichten
* `export UNIVERSEADM_STAGE=DEVELOPMENT`

### Bauen Sie das Projekt und starten Sie den Server
* `mvn -DskipTests -P'!webcomponents' package jetty:run-war`

### Öffnen Sie die Anwendung
* `http://localhost:8084/universeadm/`
- Basisauthentifizierung verwenden `Benutzer: admin | Passwort: admin`

### Projekt erstellen:
- `./mvnw clean install`
