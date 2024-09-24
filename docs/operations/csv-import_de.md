# CSV IMPORT

## Aufruf und CSV-Datei Vorgabe

Über den Endpunkt `/users/import` können Benutzer importiert werden.
Als Import-Format wird CSV nach [RFC 4180](https://datatracker.ietf.org/doc/html/rfc4180) verwendet. Der Header der Datei muss
**7** Spalten definieren:

```csv
username,displayname,givenname,surname,mail,pwdReset,external
```
Die Reihenfolge der Spalten kann variieren, jedoch müssen die Namen der Spalten beibehalten werden. 

Die Authentifizierung erfolgt über den Account des eingeloggten Benutzers. Hat dieser keine Admin-Rechte, kann der 
Endpunkt von dem Nutzer nicht aufgerufen werden. Doppelte Einträge haben keinen Einfluss auf das Ergebnis des Imports, 
werden jedoch zweifach verarbeitet. Über das Ergebnis des Imports kann festgestellt werden, welcher Eintrag fehlerhaft ist. 
Über den Import werden aktuell keine Gruppen erstellt oder zugeordnet.

## Wie der Import funktioniert.

* Über den Import können beliebig viele Nutzer angelegt werden.
* Existiert der Nutzer bereits, werden die Werte in der CSV zum Update verwendet.
* Aktuell wird jeder angelegte Nutzer als interner Nutzer angesehen.
* Über den Import kann **keine** Gruppe erstellt oder zugeordnet werden.

## E-Mail Benachrichtung
Bei der Erstellung eines neuen Benutzerkontos erhalten die Benutzer automatisch eine 
E-Mail mit ihren Anmeldeinformationen, einschließlich Benutzername und temporärem Passwort. Diese E-Mails können 
individuell konfiguriert werden, wobei Platzhalter wie ${username} und ${password} verwendet werden müssen. 
Nach dem ersten Login werden die Benutzer aufgefordert, ihr temporäres Passwort zu ändern, um die Sicherheit 
des Kontos zu gewährleisten.

## Ergebnis

Für den Import wird ein Ergebnis-Eintrag angelegt. Dieses Ergebnis ist im Volume `importHistory` unter
`/var/lib/usermgt/importHistory` zu finden. Das Ergebnis enthält eine Zusammenfassung über die Nutzer, die angelegt 
oder modifiziert wurden. Ferner enthält das Ergebnis mögliche Fehler, die während des Imports aufgetreten sind. Pro 
Eintrag wird ein Fehlercode ausgegeben:

| Code | Fehlerbeschreibung                                                                          |
|------|---------------------------------------------------------------------------------------------|
| 1000 | In der CSV-Datei fehlen Spalten, die benötigt werden.                                       |
| 1001 | Das Importergebnis konnte nicht ins Dateisystem geschrieben werden.                         |
| 2000 | Die Anzahl der Spalten einer Zeile stimmt nicht mit denen des Headers überein.              |
| 2001 | Eine benötigte Spalte war leer.                                                             |
| 2002 | Undefinierter Fehler beim Auswerten einer Zeile.                                            |
| 3000 | In der Zeile ist ein Wert enthalten, der bereits vergeben ist.                              |
| 3001 | In der Zeile wird versucht, einen Benutzer zu definieren, dessen Mail bereits vergeben ist. |
| 3002 | Die Mail des Nutzers in dieser Zeile hat ein ungültiges Format                              |
| 4000 | Eine Spalte dieser Zeile hatte einen undefinierten Formatfehler.                            |
| 4001 | Eine Spalte dieser Zeile hatte mehr als 128 Zeichen.                                        |
| 4002 | Eine Spalte dieser Zeile hatte weniger als 2 Zeichen.                                       |
| 4003 | Eine Spalte dieser Zeile enthielt ungültige Zeichen.                                        |

Neben dem Volume können Zusammenfassungen der Imports über den Endpunkt `/users/import/summaries` abgerufen werden.
Einzelne Ergebnisse sind über den Endpunkt `/users/import/{importID}` verfügbar können über `/users/import/{importID}/download`
heruntergeladen werden.

## Vollständig nutzbare CSV-Datei
```csv
username,displayname,givenname,surname,mail,pwdReset,external
dent,Arthur Dent,Arthur,Dent,arthur.dent@hitchhiker.com,false,true
trillian,Tricia McMillan,Tricia,McMillan,tricia.mcmillan@hitchhiker.com,false,true
```
