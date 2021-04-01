# Oberfläche
Wenn kein externer Verzeichnisdienst für das Cloudogu EcoSystem konfiguriert ist, kann das Dogu "User Management" benutzt werden, um Nutzerinnen und Nutzer anzulegen, Rechte zu verwalten und Gruppen zu administrieren. Wenn ein externer Verzeichnisdienst angebunden ist, wird die Verwaltung der Stammdaten darüber erfolgen.
\newline

Das User Management kann über das Warp Menü im Bereich "Administration" aufgerufen werden.

![Warp Menü mit User Management](figures/usermanagement/CESUsermanagement_Warp.png)

## Persönliche Daten
Als Nutzerin und Nutzer des Cloudogu EcoSystem können Sie Ihre persönlichen Daten und Berechtigungen im Bereich "Account" ändern.  

1. Nachdem Sie das User Management aufgerufen haben, wird automatisch der Bereich "Account" angezeigt.
2. Unter "Options" können Sie Ihre persönlichen Daten verändern.
3. Klicken Sie auf "Save", um die Änderungen zu speichern.  

Administratorinnen und Administratoren haben im User Management weitere Optionen, um Nutzerinnen und Nutzer anzulegen, deren Gruppenmitgliedschaften zu verwalten und um z.B. Passwörter zu setzen.  

![Ändern der Nutzerinnen und Nutzer Daten](figures/usermanagement/CESUsermanagement_Options.png)    

## Zugriff als Administratorin und Administrator
Als Administratorin und Administrator haben Sie zusätzlich die Möglichkeit, Nutzerinnen und Nutzer sowie Gruppen zu verwalten.

### Suche nach Nutzerinnen und Nutzern
Sofern Sie eine Vielzahl an Nutzerinnen und Nutzer administrieren müssen, hilft Ihnen die Suchfunktion.  

1. Wählen Sie den Reiter "Users" aus.

![User Management](figures/usermanagement/CESUsermanagement_Users.png)

2. Geben Sie dort im Bereich "Search" den "Username" oder den "Display Name" der zu suchenden Nutzerin oder des Nutzers ein und drücken Sie Enter.

### Daten der Nutzerinnen und Nutzer editieren
Um die Einstellungen einer Nutzerin oder eines Nutzers zu ändern, klicken Sie zunächst auf dem Reiter "Users" auf den Stift in der Spalte "Functions".

![Daten der Nutzerinnen und Nutzer ändern](figures/usermanagement/CESUsermanagement_Options.png)

Anschlie\ss end können Sie Änderungen vornehmen und diese durch einen Klick auf "Save" speichern.

### Neue Nutzerinnen und Nutzer anlegen
1. Um eine neue Nutzerin oder einen neuen Nutzer für das Cloudogu EcoSystem anzulegen, rufen Sie zunächst den Reiter "Users" auf.

![User Management](figures/usermanagement/CESUsermanagement_Users.png)

2. Klicken Sie anschlie\ss end auf den Button "Create".

![Nutzerin und Nutzer neu anlegen](figures/usermanagement/CESUsermanagement_NewUser.png)

3. Nehmen Sie die Eintragungen vor und speichern mit "Save". Konfigurierbare Eigenschaften sind:
  * Username
  * Given Name
  * Surname
  * Display name
  * Email address
  * Password  

![Nutzerin oder Nutzer neu angelegt](figures/usermanagement/CESUsermanagement_OverviewUsers.png)  

Nachdem Sie gespeichert haben, wird Ihnen die neu angelegte Nutzerin oder der Nutzer auf der Seite "Users" angezeigt. Sofern Sie noch weitere Änderungen vornehmen möchten, klicken Sie in der Spalte "Functions" auf das Symbol "Stift".

#### Eindeutige Attribute

Die E-Mail-Addresse und der Benutzername eines Nutzers dürfen nur einmal vorkommen.

Wird versucht, eine Nutzerin oder einen Nutzer mit einem Benutzernamen anzulegen, der bereits existiert, wird die folgende Fehlermeldung ausgegeben:

![Nutzerin oder Nutzer neu angelegt: Fehlermeldung Benutzername](figures/usermanagement/CESUsermanagement_UsernameUnique.png)  

Wird versucht, eine Nutzerin oder einen Nutzer mit einer E-Mail-Addresse anzulegen, die bereits existiert wird die folgende Fehlermeldung ausgegeben:

![Nutzerin oder Nutzer neu angelegt_Unique_Email: Fehlermeldung E-mail](figures/usermanagement/CESUsermanagement_EmailUnique.png)  

### Passwort-Richtlinien 
Im User Management können Passwort-Richtlinien konfiguriert werden, die während der Eingabe der Passwörter validiert werden. Durch das Anlegen von sinnvollen Passwort-Richtlinien kann die Sicherheit der Passwörter global kontrolliert werden.

##### Ablauf
1. Alle nicht erfüllten Passwort-Richtlinien werden angezeigt.

![Eine Regel erfüllt](figures/usermanagement/CESUsermanagement_Password_Policy_No_Rule_Satisfied.png)

2. Sobald eine Passwort-Richtline erfüllt wurde, wird diese grün markiert.

![Nicht alle Regeln erfüllt](figures/usermanagement/CESUsermanagement_Password_Policy_One_Rule_Statisfied.png)

3. Sobald alle Passwort-Richtlinien erfüllt wurden, kann das neue Passwort gespeichert werden.

![Alle Regeln erfüllt](figures/usermanagement/CESUsermanagement_Password_Policy_All_Rules_Satisfied.png)


##### Ausnahmen
Sollten Passwort-Richtlinien falsch konfiguriert worden sein, so werden diese angezeigt. Die Eingabe von Passwörtern im System ist dann nicht möglich. Der Administrator muss in diesem Fall die Einstellungen korrigieren.

Sollte ein ungültiger Regex zur Konfiguration verwendet worden sein, so wird eine Fehlermeldung nach dem folgenden Muster angezeigt:

![Invalid Regel](figures/usermanagement/CESUsermanagement_Password_Policy_InvalidRegex.png)


### Nutzerin oder Nutzer löschen
Klicken Sie hierzu auf der Seite "Users" auf das Symbol "Mülltonne", welches in der Spalte "Functions" und in der Zeile der zu löschenden Nutzerin oder des Nutzers abgebildet ist. Bestätigen Sie anschlie\ss end die Sicherheitsabfrage.

### Neue Gruppe erstellen
Um nicht für jede Nutzerin und Nutzer einzeln Berechtigungen vergeben zu müssen, können Gruppen angelegt werden.

1. Wählen Sie im User Management den Reiter "Groups" aus.
2. Klicken Sie auf den Button "Create".

![Gruppen erstellen](figures/usermanagement/CESUsermanagement_OptionsMembers.png)

3. Nehmen Sie Ihre Eintragungen in den Bereichen "Name" und "Description" vor.

### Gruppenzuordnung
Es gibt zwei Wege, die Zuordnung vorzunehmen:

1. Über die Änderung der Daten einer Nutzerin oder eines Nutzers:
  * Im Reiter "Users" wählen Sie für die entsprechende Nutzerin und den Nutzer in der Spalte "Functions" das Symbol "Stift" aus.
  * Anschlie\ss end wählen Sie den Reiter "Groups" und geben den entsprechenden Gruppennamen ein.
  * Danach klicken Sie auf den Reiter "Options" und dort auf "Save", um Ihre Zuordnung zu speichern.

2. Über die Gruppeneigenschaften:
  * Im Reiter "Groups" wählen Sie für die entsprechende Gruppe in der Spalte "Functions" das Symbol "Stift" aus.
  * Klicken Sie auf den Reiter "Members".
  * Geben Sie unter "Add member" den entsprechenden Username ein.
  * Klicken Sie auf den Reiter "Options" und dort auf "Save", um Ihre Zuordnung abzuschlie\ss en.

### Gruppen löschen
Klicken Sie hierzu im Bereich "Groups" unter "Functions" auf das Symbol "Mülleimer" der jeweiligen Gruppe und bestätigen Sie die Sicherheitsabfrage.

### Gruppenberechtigungen
Im User Management finden Sie unter "Groups" bereits vordefinierte Gruppen. Diese Gruppen haben bestimmte Berechtigungen in den Dogus des Cloudogu EcoSystem. \newline

**cesManager Gruppe**
Mitglieder dieser Gruppe haben vollen Zugriff auf das User Management des Cloudogu EcoSystem und können so ebenfalls Nutzerinnen und Nutzer anlegen und verwalten. \newline

**admin Gruppe** 
Mitglieder dieser Gruppe haben in allen Dogus des Cloudogu EcoSystem administrative Rechte. Das bedeutet, die Mitglieder können die einzelnen Tools administrieren und zum Beispiel Plugins installieren oder Benutzerrollen anlegen.

### CAS Übertragung von Rechten
Die Rechte der Nutzerinnen und Nutzer sowie Gruppenberechtigungen werden bei jeder Anmeldung an ein Dogu über den CAS (Central Authentication Service) neu abgefragt. Der CAS bildet das zentrale Single Sign-on Authentifizierungssystem im Cloudogu EcoSystem. Geänderte Rechte im User Management oder einem externen Verzeichnisdienst werden somit bei der nächsten Anmeldung einer Nutzerin und eines Nutzers in die jeweiligen Dogus übertragen.
