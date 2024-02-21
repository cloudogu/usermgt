# Usermgt entwickeln

## Lokales LDAP ohne CES einrichten

### Software-Anforderungen
* Die folgenden Voraussetzungen müssen erfüllt sein:
    - Oracle JDK / Open JDK 8 installieren
    - Maven installieren (mit mvn -version prüfen, ob jdk 8 korrekt eingerichtet ist / falls nicht, JAVA_HOME ändern)
    - Docker installieren

### Lokales LDAP mit Docker einrichten
* Checken Sie das folgende Repository aus https://github.com/cloudogu/docker-sample-ldap
* Gehen Sie in das eben geklonte Repository
* Bauen Sie den Container `docker build -t usermgt/ldap .`
* Starten Sie den Container `docker run --rm -p 389:389 usermgt/ldap`.


### Usermgt Entwicklungsmodus einrichten
* `export UNIVERSEADM_STAGE=DEVELOPMENT`

### In den richtigen Ordner begeben
* cd `app`

### Projekt erstellen:
- `mvn clean install`

### Bauen Sie das Projekt und starten Sie den Server
* `mvn -DskipTests -P'!webcomponents' package jetty:run-war`

### Öffnen Sie die Anwendung
* `http://localhost:8084/universeadm/`
- Basisauthentifizierung verwenden `Benutzer: admin | Passwort: admin`

## Lokales LDAP mit CES einrichten
* Binden Sie den ldap-Port an das Host-System (z.B. https://stackoverflow.com/questions/19335444/how-do-i-assign-a-port-mapping-to-an-existing-docker-container)
    - alternativ ExposedPorts zur `dogu.json` hinzufügen und den Container neu aufbauen
* Ändern Sie die `ldap.xml`-Konfiguration und stellen Sie sicher, dass das Passwort verschlüsselt ist.
    - einfache Lösung springen Sie in den usermgt-Container und kopieren Sie die `ldap.xml`
    - alternative Lösung verwenden Sie die `cipher.sh` innerhalb des usermgt-Containers ` /opt/apache-tomcat/webapps/usermgt/WEB-INF/cipher.sh encrypt <PASSWORD>`

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
