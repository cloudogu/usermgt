### Lokales LDAP ohne CES einrichten

## Das Backend lokal entwickeln

Für die Entwicklung des Usermgt-Backends müssen die folgenden Voraussetzungen erfüllt sein:

- Oracle JDK / Open JDK 8 installieren
- Maven installieren (mit mvn -version prüfen, ob jdk 8 korrekt eingerichtet ist / falls nicht, JAVA_HOME ändern)
- Docker installieren

Um das Usermgt-Backend lokal zu starten oder zu debuggen ist die Verbindung zu einem LDAP notwendig.
Dieses LDAP kann entweder auch lokal in einen Docker-Container betrieben werden, oder es kann das LDAP aus dem CES
verwendet werden.

### Lokales LDAP in einem Docker-Container einrichten

Folgende Schritte sind zum Starten des LDAP im Docker-Container nötig:

1. Repository auschecken: https://github.com/cloudogu/docker-sample-ldap
2. Den Container bauen: `docker build -t usermgt/ldap .`
3. Den Container starten: `docker run --rm -p 389:389 usermgt/ldap`.
4. Die LDAP-konfiguration für das Backend in der Datei [`app/env/data/ldap.xml`](../../app/env/data/ldap.xml) eintragen: <!-- markdown-link-check-disable-line -->
    ```xml
    <?xml version="1.0" encoding="UTF-8"?>
    <ldap>
        <host>localhost</host>
        <port>389</port>
    
        <!-- LDAP User & Password   -->
        <bind-dn>cn=usermgt_x53eMC,ou=Special Users,o=ces.local,dc=cloudogu,dc=com</bind-dn>
        <bind-password>dykIuJz9eQzylL9HLNp4xy+fjPGsNsqvzulBE7iYtMqnvusmvG6Jc4aWKTtImTxz</bind-password>
        
        <user-base-dn>ou=People,o=ces.local,dc=cloudogu,dc=com</user-base-dn>
        <group-base-dn>ou=Groups,o=ces.local,dc=cloudogu,dc=com</group-base-dn>
    
        <disable-member-listener>true</disable-member-listener>
        <disabled>false</disabled>
    </ldap>
    ```
   > Die User und Passwörter des LDAP Containers sind in
   der [README](https://github.com/cloudogu/docker-sample-ldap/blob/master/README.md) zu finden.

   > Das Passwort muss verschlüsselt sein. Dafür kann die [cipher.sh](../../app/src/main/webapp/WEB-INF/cipher.sh) <!-- markdown-link-check-disable-line -->
   verwendet werden.

### Das LDAP aus dem CES nutzen

Um für das lokale Backend des Usermgt das LDAP aus dem CES zu nutzen, sind folgende Schritte nötig:

1. Den Port des LDAP aus dem CES verfügbar machen.
   Hier gibt es zwei Möglichkeiten:
    - Den Port aus dem laufenden Container verfügbar machen. Zum Beispiel mit
      dieser [Anleitung](https://stackoverflow.com/questions/19335444/how-do-i-assign-a-port-mapping-to-an-existing-docker-container)
    - Den Port des LDAP über die `dogu.json` exposen:
      Folgenden Eintrag in der `dogu.json` des LDAP-Dogus ergänzen:
      ```json
      "ExposedPorts": [
        {
          "Type": "tcp",
          "Host": 389,
          "Container": 389
        }
      ]
      ```
      Das LDAP-Dogu mit `cesapp build ldap` neu bauen und starten.
2. Die LDAP-Konfiguration aus dem Usermgt-Dogu des CES
   auslesen: `docker exec -it usermgt cat /var/lib/usermgt/conf/ldap.xml`
3. Die LDAP-konfiguration für das Backend in der Datei [`app/env/data/ldap.xml`](../../app/env/data/ldap.xml) eintragen: <!-- markdown-link-check-disable-line -->
    ```xml
    <?xml version="1.0" encoding="UTF-8"?>
    <ldap>
      <!-- IP des lokalen CES eintragen-->
      <host>192.168.56.2</host>
      <port>389</port>
      <bind-dn>cn=usermgt_lQURMd,ou=Special Users,o=ces.local,dc=cloudogu,dc=com</bind-dn>
      <bind-password>wTyqbtiV9DdZvs0CCs8NU4MMmiRztny4PJt1sSvjz2G5zC2OVwWOoTA+Bj1R2rcE</bind-password>
      <user-base-dn>ou=People,o=ces.local,dc=cloudogu,dc=com</user-base-dn>
      <group-base-dn>ou=Groups,o=ces.local,dc=cloudogu,dc=com</group-base-dn>
      <disabled>false</disabled>
    </ldap>
    ```
   > Das Passwort ist bereits verschlüsselt und kann so übernommen werden.

### Das Usermgt-Backend lokal starten

Damit das Usermgt-Backend lokal ohne einen CAS verwendet werden kann muss die Umgebungsvariable `UNIVERSEADM_STAGE` auf
den Wert `DEVELOPMENT` gesetzt werden.

```shell
export UNIVERSEADM_STAGE=DEVELOPMENT`
```

Anschließend kann das Backend wie folgt gestartet werden:

- In das `app`-Verzeichnis wechseln: `cs app`
- Das Projekt erstellen: `mvn clean install`
- Das Projekt bauen und starten: `mvn -DskipTests -P-webcomponents package jetty:run-war`

> Hierbei wird nur das Backend neu gebaut und gestartet, das Frontend wird nicht erstellt, da das
> Maven-Profil `webcomponents` ignoriert wird.

Das Backend ist unter der URL `http://localhost:8084/usermgt/api` erreichbar

> Die Basisauthentifizierung im Entwicklungsmodus ist `Benutzer: admin | Passwort: admin`.

## Das Frontend lokal entwickeln

Das Frontend des Usermgt kann lokal entweder mit einem Mock-Backend oder mit dem lokalen Backen des Usermgt entwickelt
werden.

### Mock-Backend starten

Das Mock-Backend kann mit folgendem Befehl gestartet werden: 

```
cd app/src/main/ui
yarn backend
```

### lokales Dev-Backend starten

Das lokale Dev-Backend kann wie [oben beschrieben](#das-backend-lokal-entwickeln) eingerichtet und gestartet werden.

### Frontend starten

Damit das lokale Frontend sich beim Backend authentifizieren kann muss die Datei `.env.local` erstellt werden.
Dazu kann die Datei [`app/src/main/ui/.env`](../../app/src/main/ui/.env) als `app/src/main/ui/.env.local` kopiert werden. <!-- markdown-link-check-disable-line -->
Dort werden dann die Credentials des lokalen Backends (`Benutzer: admin | Passwort: admin`) eingetragen.


Das Frontend kann anschließend mit folgendem Befehl gestartet werden.
```
cd app/src/main/ui
yarn install
yarn dev
```

## Test-Daten für die lokale Entwicklung erstellen

Für die lokale Entwicklung können generierte Testdaten eingespielt werden.

### Test-Benutzer-Daten

- Nutzer anlegen: `create_users.py <Nutzeranzahl>`

Wird das Skript ohne Parameter aufgerufen, werden 5 Benutzer angelegt. Die Zählung beginnt immer bei 0.
Tritt ein Datenkonflikt auf, wird das Skript trotzdem fortgeführt.

Beispiel: 10 Nutzer anlegen
```shell
docs/development/create_users.py 10
```

### Test-Gruppen-Daten

- Gruppen anlegen: `create_groups.py <Gruppenanzahl>`

Wird das Skript ohne Parameter aufgerufen, werden 5 Benutzer angelegt. Die Zählung beginnt immer bei 0.
Tritt ein Datenkonflikt auf, wird das Skript trotzdem fortgeführt.

Beispiel: 10 Gruppen anlegen
```shell
docs/development/create_groups.py 10
```

## Shell-Tests mit BATS

Bash-Tests können im Verzeichnis `unitTests` erstellt und geändert werden. Das make-Target `unit-test-shell` unterstützt hierbei mit einer verallgemeinerten Bash-Testumgebung.

```bash
make unit-test-shell
```

BATS wurde so konfiguriert, dass es JUnit kompatible Testreports in `target/shell_test_reports/` hinterlässt.

Um testbare Shell-Skripte zu schreiben, sollten diese Aspekte beachtet werden:

### Globale Umgebungsvariable `STARTUP_DIR`

Die globale Umgebungsvariable `STARTUP_DIR` zeigt auf das Verzeichnis, in dem sich die Produktionsskripte (aka: Skripte-unter-Tests) befinden. Innerhalb des dogu-Containers ist dies normalerweise `/`. Aber während des Testens ist es einfacher, es aus Gründen der Berechtigung irgendwo anders abzulegen.

Ein zweiter Grund ist, dass die Skripte-unter-Tests andere Skripte quellen lassen. Absolute Pfade machen das Testen ziemlich schwer. Neue Skripte müssen wie folgt gesourcet werden, damit die Tests reibungslos ablaufen können:

```bash
source "${STARTUP_DIR}"/util.sh
```

Im obigen Beispiel dient der Kommentar zur Deaktivierung von Shellcheck. Da `STARTUP_DIR` im `Dockerfile` verdrahtet ist, wird es als globale Umgebungsvariable betrachtet, die niemals ungesetzt gefunden werden wird (was schnell zu Fehlern führen würde).

Wenn Skripte derzeit auf statische Weise gesourcet werden (d. h. ohne dynamische Variable im Pfad), macht das Shell-Tests unmöglich (es sei denn, ein besserer Weg wird gefunden, den Test-Container zu konstruieren).

Es ist eher unüblich, ein _Scripts-under-test_ wie `startup.sh` ganz alleine laufen zu lassen. Effektive Unit-Tests entwickeln sich sehr wahrscheinlich zu einem Alptraum, wenn keine ordentliche Skriptstruktur vorhanden ist. Da diese Skripte sich gegenseitig sourcen  _und_ Code ausführen, muss **alles** vorher eingerichtet werden: globale Variablen, Mocks von jedem einzelnen Binary, das aufgerufen wird... und so weiter. Am Ende würden die Tests eher auf einer End-to-End-Testebene als auf einer Unit-Test-Ebene angesiedelt sein.

Die gute Nachricht ist, dass das Testen einzelner Funktionen mit diesen kleinen Teilen möglich ist:

1. Sourcing execution guards verwenden
1. Binaries und Logikcode nur innerhalb von Funktionen ausführen
1. Sourcen mit (dynamischen, aber festgelegten) Umgebungsvariablen

#### Sourcing execution guards verwenden

Das Sourcen mit _sourcing execution guards._ kann wie folgt ermöglicht werden:

```bash
# yourscript.sh
function runTheThing() {
    echo "hello world"
}

if [[ "${BASH_SOURCE[0]}" == "${0}" ]]; then
  runTheThing
fi
```

Die folgende `if`-Bedingung wird ausgeführt, wenn das Skript durch einen Aufruf über die Shell ausgeführt wird, aber nicht, wenn es über eine Quelle aufgerufen wird:

```bash
$ ./yourscript.sh
hallo Welt
$ source yourscript.sh
$ runTheThing
Hallo Welt
$
```

_Execution guards_ funktionieren auch mit Parametern:

```bash
# yourscript.sh
function runTheThing() {
    echo "${1} ${2}"
}

if [[ "${BASH_SOURCE[0]}" == "${0}" ]]; then
  runTheThingWithParameters "$@"
fi
```

Es muss die korrekte Argumentübergabe mit `"$@"` beachtet werden, die auch solche Argumente zulässt, die Leerzeichen und dergleichen enthalten.

```bash
$ ./yourscript.sh hello world
hello world
$ source yourscript.sh
$ runTheThing hello bash
hello bash
$
```

#### Binärdateien und Logikcode nur innerhalb von Funktionen ausführen

Umgebungsvariablen und Konstanten sind in Ordnung, aber sobald Logik außerhalb einer Funktion läuft, wird sie beim Sourcen von Skripten ausgeführt.

#### Source mit (dynamischen, aber fixierten) Umgebungsvariablen

Shellcheck makert solch ein Vorgehen grundsätzlich als Fehler an. Solange der Testcontainer keine entsprechenden Skriptpfade zulässt, gibt es allerdings kaum eine Möglichkeit, dies zu umgehen:

```bash
sourcingExitCode=0
# shellcheck disable=SC1090
source "${STARTUP_DIR}"/util.sh || sourcingExitCode=$?
if [[ ${sourcingExitCode} -ne 0 ]]; then
  echo "ERROR: An error occurred while sourcing /util.sh."
fi
```

Es muss sichergestellt werden, dass die Variablen in der Produktions- (z. B. `Dockerfile`) und Testumgebung richtig gesetzt sind (hierzu eignen sich Umgebungsvariablen im Test).


