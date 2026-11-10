# Technische Konten: Architektur und API

Technische Konten ermöglichen automatisierte Zugriffe auf das Cloudogu EcoSystem unabhängig vom persönlichen Konto eines Mitarbeiters. Die Funktion verbindet ein Benutzerkonto im LDAP mit einem Zugriffsschlüssel in CAS. Im Quellcode und in den API-Pfaden werden technische Konten als `robots` bezeichnet.

Diese Dokumentation beschreibt die aktuelle Implementierung und die API für Integrationen. Eine Anleitung zur Bedienoberfläche finden Sie unter [Technische Konten](../gui/robots_de.md).

## Aufbau und Zuständigkeiten

| Bestandteil | Aufgabe |
| --- | --- |
| `RobotResource` | Stellt die HTTP-Endpunkte bereit, prüft die Berechtigung und übersetzt Servicefehler in HTTP-Antworten. |
| `RobotService` | Verknüpft LDAP-Konten mit CAS-Schlüsseln und steuert Anlegen, Erneuern und Löschen. |
| `PATClient` | Führt die HTTP-Anfragen an die PAT-API von CAS aus. |
| LDAP | Speichert das technische Benutzerkonto mit dem Attribut `technical=TRUE`. |
| CAS | Verwaltet die Zugriffsschlüssel und deren Metadaten. |
| Frontend-Service `RobotsService` | Ruft die Endpunkte des User Managements auf. |

Das Konto und sein Schlüssel sind getrennte Objekte. Beim Erneuern bleibt das LDAP-Konto bestehen, während CAS einen neuen Schlüssel erzeugt und den bisherigen löscht. Beim Löschen über die Robots-API werden dagegen der adressierte Schlüssel und das zugehörige technische Konto entfernt.

## Zugriff und Basis-URL

Die API ist unter folgender Basis-URL erreichbar:

```text
https://<ces-host>/usermgt/api/robots
```

Alle vier Operationen setzen ein authentifiziertes Konto mit der internen Rolle `Universe Administrator` voraus. Die Rollenprüfung erfolgt in jeder Methode von `RobotResource`. In der CAS-Konfiguration wird die konfigurierte Administratorrolle auf diese interne Rolle abgebildet. Für Integrationen ist diese serverseitige Rolle maßgeblich, auch wenn die Berechtigung in der Oberfläche als Managerrecht bezeichnet wird.

Die folgenden `curl`-Beispiele verwenden eine bereits authentifizierte User-Management-Sitzung. `cookies.txt` steht für eine Cookie-Datei im von `curl` unterstützten Format mit den gültigen Sitzungscookies. Die Anmeldung selbst ist nicht Teil der Robots-API. Ersetzen Sie Host, IDs und Beispieldaten durch Werte Ihrer Umgebung.

```bash
export ROBOTS_API='https://<ces-host>/usermgt/api/robots'
```

Requests mit JSON-Inhalt verwenden `Content-Type: application/json`. Antworten mit Nutzdaten werden als `application/json` ausgegeben. Nicht authentifizierte Requests können bereits vor der Resource durch die Sicherheitsfilter abgewiesen oder zur Anmeldung umgeleitet werden.

## Endpunkte im Überblick

| Methode | Pfad relativ zur Basis-URL | Funktion | Erfolgsstatus |
| --- | --- | --- | --- |
| `GET` | leer | Technische Konten mit Schlüsselmetadaten auflisten | `200 OK` |
| `POST` | leer | Technisches Konto und ersten Schlüssel anlegen | `201 Created` |
| `PUT` | `/{id}` | Einen vorhandenen Schlüssel ersetzen | `201 Created` |
| `DELETE` | `/{id}` | Schlüssel und zugehöriges technisches Konto löschen | `204 No Content` |

`id` ist die **CAS-Schlüssel-ID**, nicht der Benutzername und nicht der geheime Schlüsselwert. Kodieren Sie die ID bei Bedarf als URL-Pfadsegment. Nach dem Erneuern hat der neue Schlüssel eine neue ID.

Ein Endpunkt `GET /{id}` ist nicht implementiert. Für eine Detailansicht lädt das Frontend die Liste und sucht darin nach der Schlüssel-ID.

## Konten und Schlüsselmetadaten abfragen

```bash
curl --cookie cookies.txt \
  --header 'Accept: application/json' \
  "$ROBOTS_API"
```

Beispielantwort mit `200 OK`:

```json
[
  {
    "id": "pat-123",
    "userId": "datenabgleich",
    "displayName": "datenabgleich",
    "createdAt": "2026-11-10T10:00:00Z",
    "expiresAt": "2099-01-01T00:00:00Z",
    "scope": "/scm,/redmine"
  }
]
```

Die Werte dienen als Beispiel. Die konkreten IDs und Zeitangaben stammen aus CAS.

| Feld | Bedeutung |
| --- | --- |
| `id` | ID des Schlüssels; wird für Erneuern und Löschen verwendet. |
| `userId` | Von CAS gelieferte Benutzerzuordnung. |
| `displayName` | In der Übersicht der LDAP-Anzeigename; bei leerem Anzeigenamen wird der Benutzername verwendet. |
| `createdAt` | Erstellungszeitpunkt des Schlüssels aus CAS. |
| `expiresAt` | Ablaufzeitpunkt des Schlüssels. Ohne Ablaufdatum kann das Feld fehlen. |
| `scope` | Geltungsbereich des Schlüssels, beispielsweise `/scm,/redmine` oder `/*`. |

Die Übersicht enthält niemals das Feld `token`. Ein Feld `status` wird vom Backend ebenfalls nicht ausgegeben; das Frontend leitet den Status aus dem Ablaufdatum ab. Die Gson-Serialisierung lässt Felder mit dem Wert `null` weg.

### Wie die Liste entsteht

1. Der Service sucht unter der konfigurierten Benutzerbasis im LDAP nach dem Benutzer-Basisfilter und `technical=TRUE`.
2. Die LDAP-Suche liest Seiten mit jeweils bis zu 100 Einträgen und folgt den Paging-Cookies bis zum Ende. Benutzername und Anzeigename werden über das konfigurierte Attribut-Mapping gelesen.
3. Die gefundenen Konten werden nach Benutzername sortiert, ohne Groß- und Kleinschreibung zu unterscheiden.
4. Für jedes Konto fragt der Service die Schlüsselmetadaten bei CAS ab.
5. Pro Konto wird nur der erste Schlüssel in der von CAS gelieferten Reihenfolge in die Übersicht aufgenommen.

Konten ohne Schlüssel werden ausgelassen. Eine CAS-Antwort `204` gilt dabei als leere Schlüsselliste. Scheitert eine LDAP- oder CAS-Abfrage, schlägt der gesamte Request fehl; die API liefert keine unvollständige Übersicht zurück.

Die öffentliche API bietet keine Paging-Parameter. Ein Listenaufruf kann mehrere LDAP-Seiten und eine CAS-Anfrage pro technischem Konto auslösen. Die normale Benutzerübersicht filtert technische Konten aus.

## Technisches Konto anlegen

```bash
curl --cookie cookies.txt \
  --request POST \
  --header 'Content-Type: application/json' \
  --header 'Accept: application/json' \
  --data '{
    "displayName": "datenabgleich",
    "expiresAt": "2099-01-01T00:00:00Z",
    "scope": "/scm,/redmine"
  }' \
  "$ROBOTS_API"
```

| Request-Feld | Pflicht | Verhalten |
| --- | --- | --- |
| `displayName` | Ja | Wird zugleich als Benutzername verwendet. Erlaubt sind 2 bis 64 Zeichen aus `a–z`, `A–Z`, `0–9`, `_`, `@`, `.` und `-`. |
| `expiresAt` | Nein | ISO-8601-Zeitpunkt, der von `Instant.parse` gelesen werden kann und in der Zukunft liegen muss, zum Beispiel `2099-01-01T00:00:00Z`. Fehlend oder `null` bedeutet, dass kein Ablaufdatum an CAS gesendet wird. |
| `scope` | Im Frontend ja | Das Frontend sendet eine kommaseparierte Liste von Dogu-Pfaden oder `/*` für alle Dogus. `RobotService` validiert dieses Feld nicht selbst, sondern reicht es an CAS weiter. |

Die Zeitangabe ist ein absoluter Zeitpunkt. Angaben wie `30` oder `30 Tage` sind nicht zulässig. Die Oberfläche berechnet aus der gewählten Anzahl von Tagen einen entsprechenden Zeitpunkt.

Beispielantwort mit `201 Created`:

```json
{
  "id": "pat-123",
  "userId": "datenabgleich",
  "displayName": "datenabgleich",
  "createdAt": "2026-11-10T10:00:00Z",
  "expiresAt": "2099-01-01T00:00:00Z",
  "scope": "/scm,/redmine",
  "token": "BEISPIEL-KEIN-ECHTER-SCHLUESSEL"
}
```

Der Service übernimmt die Metadaten aus CAS und setzt den zurückgegebenen `displayName` auf den Benutzernamen. Er verlangt mindestens eine nicht leere `id` und einen nicht leeren `token` von CAS. Andere Metadaten können fehlen, wenn CAS sie nicht liefert.

Der geheime Schlüsselwert wird nur in der erfolgreichen Antwort auf das Anlegen oder Erneuern zurückgegeben. Die Robots-API bietet keine Möglichkeit, ihn später erneut abzurufen.

### Ablauf in LDAP und CAS

1. Der Service validiert Name und Ablaufdatum.
2. Er erzeugt eine eindeutige technische E-Mail-Adresse, zum Beispiel `datenabgleich-robot@account.de`. Dafür wird der Name kleingeschrieben und werden Zeichen außerhalb von `a–z` und `0–9` durch Bindestriche zusammengefasst. Bei Konflikten wird eine laufende Nummer ergänzt. Der Teil vor `@` bleibt auf höchstens 64 Zeichen begrenzt.
3. Benutzername, Anzeigename, Vorname und Nachname des neuen LDAP-Benutzers werden mit dem angegebenen Namen belegt. Die Gruppenliste ist zunächst leer; das Attribut `technical` wird auf `TRUE` gesetzt.
4. Der Service erzeugt ein zufälliges internes Passwort. Abhängig von der LDAP-Konfiguration wird es vor dem Speichern gehasht. Das Passwort wird nicht an den API-Client zurückgegeben.
5. Nach dem erfolgreichen LDAP-Anlegen erstellt `PATClient` den Schlüssel bei CAS.
6. Erst nach erfolgreicher Schlüsselerstellung wird ein `UserEvent` vom Typ `CREATE` veröffentlicht.

Scheitert die Schlüsselerstellung, versucht der Service, das gerade angelegte LDAP-Konto wieder zu löschen. Ein bereits vorhandenes Konto wird bei einem Namenskonflikt nicht verändert. Scheitert auch das Zurückrollen, wird der zusätzliche Fehler protokolliert; ein LDAP-Konto ohne nutzbaren Schlüssel kann zurückbleiben.

## Zugriffsschlüssel erneuern

Die Erneuerung ersetzt den Schlüssel eines bestehenden technischen Kontos. Sie ist keine Änderung des bestehenden Schlüsselobjekts.

```bash
curl --cookie cookies.txt \
  --request PUT \
  --header 'Content-Type: application/json' \
  --header 'Accept: application/json' \
  --data '{"expiredAt":"2099-02-01T00:00:00Z"}' \
  "$ROBOTS_API/pat-123"
```

**Der Feldname lautet hier `expiredAt`, beim Anlegen dagegen `expiresAt`.** Die abweichende Schreibweise ist Teil der aktuellen Implementierung.

| Request-Inhalt | Wirkung |
| --- | --- |
| `{"expiredAt":"2099-02-01T00:00:00Z"}` | Verwendet den angegebenen zukünftigen Ablaufzeitpunkt. |
| `{}` | Behält das bisherige Ablaufdatum bei. |
| `{"expiredAt":null}` | Behält ebenfalls das bisherige Ablaufdatum bei. |

Ein ungültiger, leerer oder nicht in der Zukunft liegender expliziter Zeitpunkt führt zu `400 Bad Request`. Die Fehlernachrichten nennen dabei `expiresAt`, weil dieselbe Validierung wie beim Anlegen verwendet wird.

**Mit `null` lässt sich bei der Erneuerung kein vorhandenes Ablaufdatum entfernen.** Ein bereits unbefristeter Schlüssel bleibt ohne expliziten Zeitpunkt unbefristet. Bei einem abgelaufenen Schlüssel sollte ein neuer zukünftiger Zeitpunkt angegeben werden: Ohne Überschreibung wird das alte Ablaufdatum an CAS weitergegeben, nicht automatisch verlängert.

Die Erfolgsantwort hat den Status `201 Created` und dieselbe Struktur wie beim Anlegen. Sie enthält die **neue ID** und den **neuen geheimen Schlüsselwert**. Verwenden Sie für spätere Operationen diese neue ID.

### Ablauf und Fehlerbehandlung

1. Der Service sucht den adressierten Schlüssel unter den Schlüsseln aller technischen LDAP-Konten. Anders als die Übersicht berücksichtigt diese Suche alle von CAS zurückgegebenen Schlüssel eines Kontos.
2. Er übernimmt Anzeigename und Geltungsbereich des alten CAS-Schlüssels und setzt gegebenenfalls den neuen Ablaufzeitpunkt.
3. CAS erstellt zunächst einen neuen Schlüssel für denselben Benutzer.
4. Anschließend wird der alte Schlüssel in CAS gelöscht.
5. Erst danach gibt die API den neuen Schlüssel zurück. Das LDAP-Konto bleibt unverändert; ein Benutzerereignis wird dabei nicht veröffentlicht.

Schlägt das Erstellen des Ersatzschlüssels fehl, wird der alte Schlüssel nicht gelöscht. Schlägt das anschließende Löschen des alten Schlüssels fehl, versucht der Service, den Ersatzschlüssel wieder zu entfernen. Auch dieser Rückrollversuch kann fehlschlagen und wird dann protokolliert.

Nach erfolgreicher Erneuerung ist der alte Schlüssel ungültig. Anwendungen müssen auf den neuen Schlüssel umgestellt werden. Die Erneuerung ist nicht idempotent: Ein erneuter Request mit der alten ID kann nach erfolgreichem Abschluss `404` liefern. Geht die Erfolgsantwort verloren, kann der neue Schlüsselwert nicht über die Übersicht nachgeladen werden.

## Technisches Konto löschen

```bash
curl --cookie cookies.txt \
  --request DELETE \
  "$ROBOTS_API/pat-123"
```

Bei Erfolg antwortet die API mit `204 No Content` ohne Antwortinhalt. Zum Löschen verwenden Sie die aktuelle Schlüssel-ID, nach einer Erneuerung also die ID des Ersatzschlüssels.

Der Service ermittelt das technische Konto anhand der Schlüssel-ID und liest es erneut aus LDAP. Danach löscht er zuerst den adressierten Schlüssel in CAS, anschließend das Konto in LDAP und veröffentlicht ein `UserEvent` vom Typ `REMOVE`.

Scheitert das Löschen des Schlüssels, bleibt das LDAP-Konto erhalten. Scheitert das LDAP-Löschen nach erfolgreichem CAS-Löschen, ist der Schlüssel bereits entfernt, während das Konto noch existieren kann. Dafür gibt es keinen automatischen Wiederherstellungsversuch. Ein `REMOVE`-Ereignis wird nur nach erfolgreichem LDAP-Löschen veröffentlicht.

Die Operation löscht ausdrücklich den adressierten CAS-Schlüssel. Sie führt keine Schleife zum Löschen weiterer Schlüssel desselben Kontos aus. Das reguläre Anlegen und Erneuern ist auf einen verwalteten Schlüssel pro Konto ausgelegt.

## Statuscodes und Fehlerantworten

| Status | Operationen | Bedeutung |
| --- | --- | --- |
| `200 OK` | `GET` | Metadatenliste erfolgreich geladen, gegebenenfalls `[]`. |
| `201 Created` | `POST`, `PUT` | Konto mit Schlüssel angelegt beziehungsweise Ersatzschlüssel erstellt. |
| `204 No Content` | `DELETE` | Schlüssel und Konto erfolgreich gelöscht. |
| `400 Bad Request` | `POST`, `PUT`, `DELETE` | Servicevalidierung fehlgeschlagen, beispielsweise ungültiger Name, Zeitpunkt oder leere Schlüssel-ID. |
| `403 Forbidden` | Alle | Die Resource erkennt keine authentifizierte Identität mit der erforderlichen Rolle. Vorgeschaltete Filter können eine andere Antwort liefern. |
| `404 Not Found` | `PUT`, `DELETE` | Unter den technischen Konten wurde kein passender Schlüssel gefunden; beim Löschen gegebenenfalls auch kein zugehöriger LDAP-Eintrag mehr. |
| `409 Conflict` | `POST` | Der LDAP-Benutzername ist bereits vergeben, auch durch ein normales Benutzerkonto. |
| `500 Internal Server Error` | Alle | Fehler beim LDAP-Zugriff. |
| `502 Bad Gateway` | Alle | Fehler beim Zugriff auf CAS oder ungültige beziehungsweise unvollständige CAS-Antwort. |

Beispiele für Fehlerantworten:

```json
{"message":"displayName must be a valid username with 2 to 64 characters"}
```

```json
{"message":"A user with this name already exists"}
```

```json
{"message":"Failed to renew robot PAT in CAS"}
```

Validierungs- und Servicefehler enthalten üblicherweise ein `message`-Feld. Die direkt erzeugten Antworten für `403` und `404` haben keinen Body. Fehler beim Einlesen des JSON oder bei vorgeschalteten Filtern können vom Framework erzeugt werden und anders aussehen.

CAS-Fehler werden nicht unverändert mit ihrem ursprünglichen HTTP-Status weitergereicht. Beispielsweise wird ein von CAS abgelehnter Erstellungsrequest durch `RobotService` als Fehler behandelt und von der Resource mit `502` beantwortet.

## Interne CAS-Requests

`PATClient` ergänzt an die konfigurierte CAS-Server-URL den Pfad `/api/users` und verwendet folgende Endpunkte:

| Zweck | Interner Request |
| --- | --- |
| Schlüsselmetadaten lesen | `GET <cas-server-url>/api/users/{username}/pats` |
| Schlüssel erstellen | `POST <cas-server-url>/api/users/{username}/pats` |
| Schlüssel löschen | `DELETE <cas-server-url>/api/users/{username}/pats/{id}` |

Der Benutzername wird als URL-Pfadsegment kodiert. Beim Erstellen werden `displayName`, gegebenenfalls `expiresAt` und `scope` als JSON gesendet. Auch bei einer Erneuerung lautet das interne CAS-Feld `expiresAt`: Die Übersetzung von `expiredAt` übernimmt `RobotService`.

Für CAS-Anfragen liest `PATClient` die Java-Systemeigenschaften `cas.mfa.user` und `cas.mfa.password`. Sind beide gesetzt, sendet er HTTP Basic Authentication mit diesen Dienstzugangsdaten. Diese Zugangsdaten sind von der Sitzung des API-Aufrufers und vom neu erzeugten Zugriffsschlüssel getrennt. Verbindungs- und Lese-Timeout sind jeweils auf zehn Sekunden gesetzt.

## Grenzen und Hinweise für Integrationen

- LDAP und CAS bilden keine gemeinsame Transaktion. Die beschriebenen Rückrollversuche begrenzen Teilzustände, können diese aber bei weiteren Fehlern nicht vollständig verhindern.
- `POST` legt bei jedem Aufruf ein Konto an und vergibt keine Idempotenz-ID. Nach einem Verbindungsabbruch sollte vor einem erneuten Versuch der tatsächliche Zustand geprüft werden. Ein wiederholter Aufruf mit demselben Namen kann `409` ergeben.
- Die Metadatenübersicht zeigt höchstens einen Schlüssel je Konto. Sie ist keine vollständige Auflistung aller in CAS vorhandenen Schlüssel.
- Die Zuordnung für Erneuern und Löschen wird aus dem technischen LDAP-Konto und dessen CAS-Schlüsseln ermittelt. Ein `userId` aus CAS wird dafür nicht als alleinige vertrauenswürdige Quelle verwendet.
- Neue technische Konten erhalten durch diese Implementierung keine Gruppenmitgliedschaften. Die Dogu-Auswahl eines Schlüssels ist von den fachlichen Berechtigungen innerhalb eines Dogus zu unterscheiden.
- Schlüsselwerte sollten von API-Clients sicher gespeichert und nicht in Anwendungsprotokolle geschrieben werden. Verlorene Schlüsselwerte müssen durch eine erneute Erneuerung ersetzt werden.

## Relevante Quelltexte und Tests

- [RobotResource.java](../../app/src/main/java/de/triology/universeadm/robots/RobotResource.java): Endpunkte, Rollenprüfung und Statuscodes.
- [RobotService.java](../../app/src/main/java/de/triology/universeadm/robots/RobotService.java): LDAP-/CAS-Abläufe, Validierung und Rückrollversuche.
- [CreateRobotRequest.java](../../app/src/main/java/de/triology/universeadm/robots/CreateRobotRequest.java) und [RenewRobotRequest.java](../../app/src/main/java/de/triology/universeadm/robots/RenewRobotRequest.java): Request-Felder.
- [RobotPAT.java](../../app/src/main/java/de/triology/universeadm/robots/RobotPAT.java) und [CreatedRobotPAT.java](../../app/src/main/java/de/triology/universeadm/robots/CreatedRobotPAT.java): Antwortfelder.
- [PATClient.java](../../app/src/main/java/de/triology/universeadm/pat/PATClient.java): HTTP-Anbindung an CAS.
- [Robots.ts](../../app/src/main/ui/src/services/Robots.ts): Frontend-Aufrufe der API.
- [RobotResourceTest.java](../../app/src/test/java/de/triology/universeadm/robots/RobotResourceTest.java) und [RobotServiceTest.java](../../app/src/test/java/de/triology/universeadm/robots/RobotServiceTest.java): Tests der Statuscodes, Validierung und Fehlerbehandlung.
