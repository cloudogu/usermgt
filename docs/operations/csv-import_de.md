# CSV IMPORT

## Aufruf und CSV-Datei Vorgabe

Über den Endpunkt `/users/import` können Benutzer importiert werden.
Als Import-Format wird CSV verwendet. Der Header der Datei muss mindestens **6** Spalten definieren.
Empfehlenswert ist: 
```csv
Username;FirstName;Surname;DisplayName;Email;Groups
```
Dies ist die Reihenfolge, in welcher die Werte eingelesen werden. Dabei ist nur die Reihenfolge wichtig, die Werte in der 
ersten Spalte können frei gewählt werden. 
Daher könnten diese Spalten auch in Deutsch sein, zum Beispiel:
```csv
Nutzername;Vorname;Nachname;Anzeigetitel;Mail;Gruppn
```

Die Authentifizierung läuft über den Account des eingeloggten Benutzers. Hat dieser keine Manager-Rechte, so kann dieser 
Endpunkt von dem Nutzer nicht aufgerufen werden. Doppelte Einträge werden herausgefiltert und über das Protocol kann 
festgestellt werden, wenn ein Eintrag fehlerhaft ist. Gruppen werden nur zugeordnet, wenn sie bereits im System existieren.
Es werden dabei keine neuen Gruppen automatisch angelegt.
 
## Wie der Import funktioniert.

* Über den Import können beliebig viele Nutzer angelegt werden.
* Über den Import kann **nicht** eine Gruppe angelegt werden.
* Über den Import kann ein neuer oder bestehender Nutzer einer Gruppe zugewiesen werden.
  * Um einen bestehenden Nutzer einer Gruppe hinzuzufügen, muss nur der Nutzername und die Gruppen in die Zeile 
  geschrieben werden.
  * Beispiel: `Tester3;;;;G1,G2,G3,G4`

## Grund der Notwendigkeit

Wenn es mehrere neue Mitarbeiter gibt oder das CES in einem Unternehmen initial aufgesetzt wird, müssen mehrere 
Nutzeraccounts erstellt werden. Um den Administratoren, beziehungsweise den Managern des CES den Aufwand der manuellen 
Anlegung zu ersparen. Über den Import können die Nutzer kompakt und effizient aufgelistet werden und in wenigen Sekunden 
werden alle Nutzer angelegt.

## Protocol

Für den Import wird ein Protocol-Eintrag angelegt. Dieses Protokoll ist im Volume user-import-protocol unter
`/var/lib/usermgt/protocol/user-import-protocol` zu finden. Für jeden Nutzer und für jede Gruppenzuweisung wird ein 
Eintrag über den Status der Durchführung erstellt. Der Status kann erfolgreich, fehlerhaft oder existiert bereits sein.

## Email

Für jeden erstellten Nutzer wird diesem eine Mail mit seinen Nutzerdaten verschickt. In der Konfigurationsdatei des UserMgt
kann der `Host` und `Port` definiert werden. Über die Konfigurationsschlüssel `import/mail/subject` und `import/mail/content`
können weitere Einstellungen vorgenommen werden.

## Vollständig nutzbare CSV-Datei
```csv
Username;FirstName;Surname;DisplayName;Email;Groups
Tester1;Tes;Ter;Tester1;test1@test.com;G1,G2   
Tester2;Tes;Ter2;Tester2;test2@test.com;G2,G3
Tester3;Tes;Ter3;Tester3;test3@test.com;G1,G3
Tester4;Tes;Ter4;Tester4;test4@test.com;G4,G1
```