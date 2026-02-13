# Release Notes

Im Folgenden finden Sie die Release Notes für das User Management. 

Technische Details zu einem Release finden Sie im zugehörigen [Changelog](https://docs.cloudogu.com/de/docs/dogus/usermgt/CHANGELOG/).

## [Unreleased]
### Security
- [#212] Sicherheitslücke geschlossen [cve-2025-68121](https://avd.aquasec.com/nvd/2026/cve-2026-68121/)
- [#204] Sicherheitslücke geschlossen [cve-2026-24515](https://avd.aquasec.com/nvd/2026/cve-2026-24515/)

## [v1.20.1-2] - 2026-01-29
### Security
- [#204] Sicherheitslücke geschlossen [cve-2025-15467](https://avd.aquasec.com/nvd/2025/cve-2025-15467/)

## [v1.20.1-1] - 2025-12-12
- Als fälschlich ausgewiesene Authentifizierungsfehler werden von der User-REST-API nun korrekt als interne Serverfehler gemeldet. Dies könnten z. B. mangelnde CAS-Konnektivität, Throttling oder LDAP-Zeitüberschreitungen sein.

## [v1.20.0-5] - 2025-04-25
### Changed
- Die Verwendung von Speicher und CPU wurden für die Kubernetes-Multinode-Umgebung optimiert.

## [v1.20.0-4] - 2025-04-10
### Security
* Das Release behebt die kritische Sicherheitslücke [CVE-2025-24813](https://nvd.nist.gov/vuln/detail/CVE-2025-24813).
  Ein Update ist daher empfohlen.

## [v1.20.0-3] - 2025-02-21
### Changed
- Der Tabellenheader "Datum" wurde in "Importdatum" umbenannt

## [v1.20.0-2] - 2025-02-13
Wir haben nur technische Änderungen vorgenommen. Näheres finden Sie in den Changelogs.

## [v1.20.0-1] - 2025-01-27
Wir haben nur technische Änderungen vorgenommen. Näheres finden Sie in den Changelogs.

## [v1.19.0-1] - 2025-01-22
* Alle Pflichtfelder im Formular zum Anlegen/Editieren von Nutzern sind jetzt als verpflichtend markiert und es ist 
  nicht mehr möglich, das Formular abzusenden, wenn nicht alle verpflichtenden Felder ausgefüllt sind

## [v1.18.0-1] - 2025-01-17
### Changed
* Die internen Makefiles wurden aktualisiert um die Versionierung der Release-Notes zu vereinheitlichen.
### Added
* Optionaler sync-Queryparameter am create-User Endpunkt
    * Benutzer und Gruppen werden synchron angelegt, wenn der Parameter angegeben wird
    * Eine Anleitung befindet sich in create_users_synced.md

## Release 1.17.2-1
* In der Benutzerliste werden nun keine Mail-To Links mehr angezeigt 

## Release 1.17.1-1
* Behebung eines Bugs: In der 1.17.0-1 werden fälschlicherweise auch nicht-externe Nutzer als externe Nutzer markiert. Ab dieser Version werden nur externe Nutzer als externe Nutzer angegeben.

## Release 1.17.0-1
* Externe Nutzer werden als solche in der Übersicht markiert
  * In der Übersichtstabelle der Nutzer wird eine weitere Spalte angezeigt, die angibt ob ein Nutzer extern oder intern ist
    * Diese Spalte wird nur angezeigt, wenn mindestens ein externer Benutzer vorhanden ist
  * Externe Nutzer können nicht editiert werden. Alle Felder werden entweder ausgeblendet oder deaktiviert
  * Gruppen können weiterhin zu externen Nutzern hinzugefügt und entfernt werden

## Release 1.16.4-1
* Auf der Nutzerimport-Vorschauseite sind nun 25 Einträge pro Seite statt der vorherigen 8 Einträge
* Mails mit mehr als einem '@' sind nun nicht mehr zulässig

## Release 1.16.3-1
* Die Fehler der Nutzer-Importergebnisse sind jetzt korrekt aufsteigend nach der Zeilennummer sortiert.

## Release 1.16.2-1
* Behebung eines Rechtschreibfehlers in den Fehlernachrichten des Imports

## Release 1.16.1-1
* Verbesserung von Fehlernachrichten im CSV-Import-Prozess.

## Release 1.16.0-1
* Die Cloudogu-eigenen Quellen werden von der MIT-Lizenz auf die AGPL-3.0-only relizensiert.

## Release 1.15.4-1
* Verbesserung der Benutzerfreundlichkeit des User-Imports
  * Die Fehlermeldungen in den Importübersichten sind jetzt aussagekräftiger und ähneln mehr denen bei der normalen Benutzererstellung

## Release 1.15.3-1
* Verbesserung von Fehlernachrichten sowohl in manuellen Kontenbearbeitungen als auch CSV-Import-Prozessen.
* Die Überschrift der Seite zur Änderung der eigenen Daten ("Account") wird zu "Mein Account" umbenannt
* Bezeichner rund um die Zuordnung von Benutzer:innen zu Gruppen werden so umbenannt, das klar wird, dass damit keine neuen Konten oder Gruppen angelegt werden.

## Release 1.15.2-1
* Behebung des kritischen CVEs CVE-2024-41110 in Bibliotheksabhängigkeiten. Diese Schwachstelle konnte im Usermanagement jedoch nicht aktiv ausgenutzt werden. 

## Release 1.15.1-1
* Optionale Felder sind nun beim Anlegen bzw. Bearbeiten von Accounts oder Gruppen als "optional" markiert.

## Release 1.15.0-1

Wir haben nur technische Änderungen vorgenommen. Näheres finden Sie in den Changelogs.

## Release 1.14.3-2

* Verbesserung der Usability: Bei Eingaben zum Anlegen oder Bearbeiten von Gruppen bzw. Accounts wird beim Speichern Rückmeldung gegeben, wenn diese inkorrekt sind.

## Release 1.14.3-1

* Erweiterung der E-Mail-Validierung: Es können nun auch E-Mail-Adressen mit Zahlen für Accounts hinterlegt werden.
* Anpassung des Gruppen-Managements: Gruppen dürfen keine Leerzeichen enthalten. Durch alte Versionen können Gruppen mit Leerzeichen im System vorhanden sein. Diese können nun wieder bearbeitet werden.
