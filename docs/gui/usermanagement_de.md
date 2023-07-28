# User Management

Das **User Management** ist ein Dogu zum **Verwalten der Nutzer:innen und Gruppen eines EcoSystems**. Neben den eigenen persönlichen Daten können Sie hier, wenn Sie die benötigten Rechte haben, Nutzer:innen sowie Gruppen anlegen, bearbeiten und löschen.

Das **User Management** erreichen Sie über das Warp Menü im Bereich „Administration“ aufgerufen werden.

![Warp Menü mit User Management](figures/usermanagement/CESUsermanagement_Warp_de.png)

## Persönliche Daten

Als Nutzerin oder Nutzer des Cloudogu EcoSystem können Sie **Ihre persönlichen Daten** im Account-Bereich ändern.

![Bild von Seitenkopf mit Fokus auf Navbar mit eingeloggtem Benutzer](figures/usermanagement/CESUsermanagement_UserAccount_de.png)

Der eigene Account-Bereich wird Ihnen beim Öffnen des **User Managements** direkt angezeigt. Über das 
Formular können Sie Ihre persönlichen Daten wie Ihre E-Mail-Adresse oder Ihr Passwort anpassen. Die Anpassungen, die Sie vornehmen, werden erst **mit einem Klick auf den Speichern-Button aktualisiert**. Der Nutzername kann nicht geändert werden.

Im unteren Teil des Account-Bereichs können Sie Ihre eigenen Gruppen sehen.

![Bild von zugeordneten Gruppen](figures/usermanagement/CESUsermanagement_AssignedGroups_de.png)

## Verwaltung von Accounts

Als Administrator:in haben Sie im **User Management** die Möglichkeit, **Nutzer:innen anzulegen, zu löschen und zu bearbeiten**.

### Suche nach Accounts

Sofern Sie eine Vielzahl an Nutzer:innen administrieren, hilft Ihnen die **Suchfunktion**, einen gewünschten Account zu finden.

1. Wählen Sie den Reiter „Nutzer“ aus.

![Nutzerübersicht mit zwei Nutzern](figures/usermanagement/CESUsermanagement_Users_de.png)

2. Geben Sie dort im Bereich "Filter" Nutzername, Anzeigename oder E-Mail-Adresse des Account ein, den Sie finden möchten, und drücken Sie *Enter*. Ihnen werden in der Tabelle dann nur noch die Accounts angezeigt, deren Nutzername, Anzeigename oder deren E-Mail-Adresse zu Ihrer Sucheingabe passt.

![Nutzerübersicht gefiltert nach Nutzer testuser](figures/usermanagement/CESUsermanagement_UsersSearchResult_de.png)

Um den Filter wieder zu entfernen, klicken Sie auf das „X“-Symbol innerhalb des Suchfeldes.

### Änderung von Account-Daten

Um die Daten eines Accounts zu ändern, klicken Sie zunächst im Reiter „Nutzer“ auf das Stift-Symbol in der Zeile des Accounts, den Sie ändern möchten.

![Daten der Nutzerinnen und Nutzer ändern](figures/usermanagement/CESUsermanagement_EditUser_de.png)

Danach können Sie Änderungen vornehmen und diese durch einen Klick auf den „Speichern“-Button sichern. 
Neben den Account-Informationen wie E-Mail-Adresse oder Anzeigename, können Sie auch das Passwort anpassen. Zusätzlich dazu bietet das **User Management** die Möglichkeit, dass die Nutzerin oder der Nutzer beim nächsten Login das Passwort  ändern muss. 

Beachten Sie, dass Sie den Nutzernamen **nicht** ändern können.

### Löschen von Accounts

Klicken Sie zum Löschen eines Accounts auf der Seite „Nutzer“ auf das Mülltonnen-Symbol in der Zeile des Accounts, den Sie löschen wollen. Es folgt eine Sicherheitsabfrage, die Sie bestätigen müssen, bevor der Account endgültig gelöscht wird.

Beachten Sie, dass im **User Management** gelöschte Accounts nicht automatisch auch in den Dogus gelöscht werden.

![Account löschen](figures/usermanagement/CESUsermanagement_DeleteUser_de.png)

### Anlegen neuer Accounts

Um einen neuen Account für das Cloudogu EcoSystem anzulegen, rufen Sie zunächst den Reiter „Nutzer“ auf und klicken auf den Button „Nutzer anlegen“.

![User Management](figures/usermanagement/CESUsermanagement_Users_de.png)

Ein Formular öffnet sich, in dem Sie folgende Eigenschaften des neuen Accounts eintragen können:

* Nutzername* (für die Anmeldung am Cloudogu EcoSystem)
* Vorname
* Nachname
* Anzeigename (angezeigter Name des Nutzers / der Nutzerin in den einzelnen Dogus)
* E-Mail* (Nutzer / Nutzerin wird über diese E-Mail benachrichtigt)
* Passwort (für die Anmeldung am Cloudogu EcoSystem)
* „Nutzer muss sein Passwort beim nächsten Login ändern“ (Wird diese Option aktiviert, muss die Nutzerin / der Nutzer bei der nächsten Anmeldung sein Passwort ändern)

\* E-Mail-Adresse und Nutzername eines Nutzers / einer Nutzerin sind **eindeutige Eigenschaften** und dürfen daher nur für einen Account verwendet werden. Beim Anlegen eines Accounts wird überprüft, ob E-Mail-Adresse und Nutzername eindeutig sind. Sollte das nicht der Fall sein, bekommen Sie eine aussagekräftige Fehlermeldung angezeigt und können die Angaben des Accounts überarbeiten.

> Beachten Sie, dass der Nutzername nach der Anlage der Nutzerin oder des Nutzers **unveränderlich** ist.

![Nutzerin und Nutzer neu anlegen](figures/usermanagement/CESUsermanagement_NewUser_de.png)

Den Account legen Sie mit einem Klick auf den „Speichern“-Button an.

![Nutzerin oder Nutzer neu angelegt](figures/usermanagement/CESUsermanagement_NewUserCreated_de.png)

Nachdem Sie gespeichert haben, wird Ihnen der neu angelegte Account auf der Seite „Nutzer“ angezeigt. 
Sofern Sie noch weitere Änderungen vornehmen möchten, klicken Sie in der letzten Spalte auf das Stift-Symbol.

### Passwort-Richtlinien

In der Konfiguration des Cloudogu EcoSystem können **Passwort-Richtlinien** konfiguriert werden, die bei Eingabe der Passwörter validiert werden. 
Durch das Anlegen von sinnvollen Passwort-Richtlinien kann die Sicherheit der Passwörter global kontrolliert werden.

Wenn Sie ein Passwort anlegen, bekommen Sie immer die Passwort-Richtlinien angezeigt, die noch nicht erfüllt worden sind. Sobald eine Passwort-Richtlinie erfüllt ist, wird diese nicht mehr angezeigt.

![Nicht alle Regeln erfüllt](figures/usermanagement/CESUsermanagement_Password_Policy_Not_All_Rules_Statisfied_de.png)

Sie können das Passwort erst speichern, wenn alle Passwort-Richtlinien erfüllt worden sind.

Sind alle Passwort-Richtlinien erfüllt, wird das Feld grün markiert und das neue Passwort kann gespeichert werden.

![Alle Regeln erfüllt](figures/usermanagement/CESUsermanagement_Password_Policy_All_Rules_Satisfied_de.png)

## Verwaltung von Gruppen

Als Administrator:in haben Sie im **User Management** die Möglichkeit, **Gruppen anzulegen, zu bearbeiten oder zu löschen sowie die Mitglieder von Gruppen zu verwalten**.

Gruppen können zur Verwaltung von unterschiedlichen Rechtekonfigurationen für unterschiedliche Nutzergruppen verwendet werden. Mehr Informationen dazu finden Sie im Abschnitt [Rechtekonzept im Cloudogu EcoSystem](#rechtekonzept-im-cloudogu-ecosystem).

### Systemgruppen

Im **User Management** finden Sie zwei vordefinierte Gruppen. Mitglieder dieser Gruppen haben besondere Berechtigungen in den Dogus des Cloudogu EcoSystem.

**Manager-Gruppe**

Mitglieder der **Manager-Gruppe** haben **vollen Zugriff auf das User Management** des Cloudogu EcoSystems.
Damit erhalten Nutzer:innen die Berechtigung, weitere Nutzer:innen und Gruppen anzulegen und zu verwalten.
Darüber hinaus sind keine weiteren Berechtigungen mit der *Manager-Gruppe* verbunden.

Sie können die zu verwendende *Manager-Gruppe* ändern, indem Sie in der Konfiguration des Cloudogu EcoSystem im Eintrag 
`/config/_global/manager_group` die gewünschte Gruppe einstellen:

```shell
etcdctl set /config/_global/manager_group neueManagerGruppe
```

Das **User Management**-Dogu muss anschließend neu gestartet werden, damit die Änderung wirksam wird.

**Admin-Gruppe**

Mitglieder dieser Gruppe haben in **allen** Dogus des Cloudogu EcoSystem administrative 
Berechtigungen. Diese Nutzer:innen können in den einzelnen Dogus die administrativen Funktionen nutzen 
und so zum Beispiel Plugins installieren oder Anwendungseinstellungen vornehmen.

Das **Backup & Restore**-Dogu enthält ausschließlich administrative Funktionen und verwendet kein separates Rollenkonzept.
Somit haben nur Nutzer und Nutzerinnen auf das **Backup & Restore**-Dogu Zugriff, die Mitglied der **Admin-Gruppe** sind.

Sie können die zu verwendende **Admin-Gruppe** ändern, indem Sie in der Konfiguration des Cloudogu EcoSystem im Eintrag
`/config/_global/admin_group` die gewünschte Gruppe einstellen:

```shell
etcdctl set /config/_global/admin_group neueAdminGruppe
```

Es müssen anschließend alle Dogus neu gestartet werden, damit die Änderung wirksam wird.

### Anlegen einer neuen Gruppe

Um nicht für alle Nutzer:innen einzelne Berechtigungen in den Dogus vergeben zu müssen, können Sie - wie im Folgenden beschrieben - Gruppen angelegen:

1. Wählen Sie im **User Management** den Reiter „Gruppen“ aus.

![Kopfzeile der Gruppenübersicht](figures/usermanagement/CESUsermanagement_GroupsOverviewHeader_de.png)

2. Klicken Sie auf den Button „Gruppe anlegen“.

![Leeres Gruppenformular](figures/usermanagement/CESUsermanagement_NewGroup_de.png)

3. Definieren Sie die Eigenschaften der neuen Gruppe:
  * Name*
  *  Beschreibung

  \* Der Name einer Gruppe ist eine **eindeutige Eigenschaften** und darf daher nur für eine Gruppe verwendet werden. Beim Anlegen einer Gruppe wird überprüft, ob der Name eindeutig ist. Sollte das nicht der Fall sein, bekommen Sie eine aussagekräftige Fehlermeldung angezeigt und können den Namen der Gruppe überarbeiten.

> Beachten Sie, dass der Gruppenname nach der Anlage der Gruppe unveränderlich ist.

4. Legen Sie die Gruppe mit einem Klick auf den „Speichern“-Button an.

### Gruppenzuordnung

Es gibt zwei Wege, einen Account einer Gruppe zuzuordnen:
* Über das Bearbeiten des Accounts
* Über das Bearbeiten der Gruppe

**Gruppenzuordnung über das Bearbeiten eines Accounts:**

1. Im Reiter „Nutzer“ wählen Sie für den entsprechenden Account das Stift-Symbol aus.

2. Danach tragen sie im Bereich „Gruppen“ unter *Gruppe hinzufügen* den Gruppennamen ein. Es erscheint automatische eine Liste mit Vorschlägen entsprechend der getätigten Eingabe. 
3. Mit einem Klick auf die gewünschte aufgeführte Gruppe ordnen Sie die Gruppe dem Account zu.

![](figures/usermanagement/CESUsermanagement_AssignGroups_de.png)

4. Mit einem Klick auf den „Speichern“-Button werden die Änderungen gespeichert.

**Gruppenzuordnung über das Bearbeiten einer Gruppe:**

1. Im Reiter „Gruppen“ wählen Sie für die entsprechende Gruppe das Stift-Symbol aus.
2. Im Bereich „Mitglieder“ können Sie den Nutzernamen des gewünschten Mitglieds hinzufügen. Es erscheint automatische eine Liste mit
  Vorschlägen entsprechend der getätigten Eingabe. 
3. Mit einem Klick auf den gewünschten Nutzernamen in der 
  Vorschlagsliste wird der Account der Gruppe zugeordnet.

![](figures/usermanagement/CESUsermanagement_AssignUsers_de.png)

4. Mit einem Klick auf den „Speichern“-Button wird die Zuordnung gespeichert.

### Löschen einer Gruppe

Klicken Sie zum Löschen einer Gruppe im Bereich „Gruppen“ auf das Mülleimer-Symbol der jeweiligen Gruppe und bestätigen Sie die Sicherheitsabfrage.

Systemgruppen können **nicht** gelöscht werden.

Beachten Sie, dass im **User Management** gelöschte Gruppen nicht automatisch auch in den Dogus gelöscht werden. Die Zuordnung von Accounts zu Gruppen wird aber synchronisiert.

## Rechtekonzept im Cloudogu EcoSystem

Das Rechtekonzept des Cloudogu EcoSystems basiert auf einer **zentralen Benutzerverwaltung** und einer **dezentralen Rechtekonfiguration**: Nutzer:innen und Gruppen können im **User Management** hinterlegt werden. Diese werden den anderen Dogus des Cloudogu EcoSystems bekannt gemacht, wodurch Sie in jedem Dogu die Rechtezuordnung dezentral für Gruppen oder einzelne Nutzer:innen vornehmen können.

### Benutzerverwaltung

Das **User Management** dient der Verwaltung von Accounts und Gruppen. Dabei nutzt das **User Management** ein internes *LDAP* als Verzeichnisdienst.

Neben der Nutzung des von uns zu Verfügung gestellten **User Managements** haben Sie die Möglichkeit, einen **externen Verzeichnisdienst** für das Cloudogu EcoSystem zu verwenden. In diesem Fall würde die Benutzerverwaltung **nicht** über das hier vorgestellte Dogu **User Management** erfolgen, sondern über den von Ihnen angebundenen externen Verzeichnisdienst (wie ein externes *LDAP* oder *Active Directory*).

### Rechtekonzepte der Dogus

Die Accounts für Nutzer:innen des Cloudogu EcoSystems können Sie zentral im **User Management** anlegen. Dabei können Sie zur Vereinfachung der Rechtekonfiguration für unterschiedliche Benutzergruppen Gruppen anlegen. Eine Nutzerin / ein Nutzer kann dabei mehr als einer Gruppe zugehörig sein. In einer Gruppe kann mehr als ein Mitglied sein.

Accounts und Gruppen werden **mit den Dogus synchronisiert**, so dass Sie in jedem Dogu die im **User Management** angelegten Accounts und Gruppen vorfinden. 

 Da es sich bei Dogus um Systeme handeln kann, die außerhalb des Cloudogu EcoSystems entwickelt werden, können sich die **Rechtekonzepte der Dogus unterscheiden** - wie Sie im folgenden Schaubild erkennen können.

 ![Rechtekonzept in den Dogus](figures/usermanagement/RoleConceptCloudoguEcoSystem_de.png)

 Weitere Informationen zum Rechtekonzept einzelner Dogus finden Sie in der Dokumentation des jeweiligen Dogus.

 Sie können auch direkt in den Dogus Accounts und Gruppen anlegen. Beachten Sie dabei, dass diese dann anderen Dogus nicht bekannt sind und auch nicht im **User Management** verwaltet werden können. Die Anlage von Accounts oder Gruppen außerhalb des **User Managements** ist daher **nicht** empfohlen.

### Synchronisation von Accounts und Gruppen

Nutzeraccounts und Gruppen werden an ein Dogu weitergegeben, **sobald   die Nutzerin / der Nutzer mit dem jeweiligen Account das Dogu aufruft**. Dafür werden Gruppenzuordnungen der Nutzer:innen bei jeder Anmeldung an ein Dogu über den CAS (Central Authentication Service) - der zentrale *Single Sign-on* Authentifizierungsdienst des Cloudogu EcoSystem - neu abgefragt.

Wie in folgendem Schaubild erkennbar, übergibt der CAS nach erfolgreicher Authentifizierung die benötigten Nutzerinformationen an das Dogu. Das Dogu überprüft im Anschluss, ob der Account und die Gruppen des Nutzers / der Nutzerin schon bekannt sind. Sollte das nicht der Fall sein, werden Account und Gruppen angelegt und verknüpft, wenn das Dogu dieses Vorgehen unterstützt.

In Dogus, die keine Gruppen oder Accounts verwalten, werden auch keine Gruppen oder Accounts hinterlegt. Für andere Dogus übernimmt das Anlegen von Accounts und Gruppen oft ein CAS-Plugin, welches für diesen Zweck konzipiert worden ist. In Ausnahmefällen kann es vorkommen, dass ein automatisiertes Anlegen von Gruppen in einem Dogu nicht möglich ist.

 ![Synchronisation von Accounts und Gruppen](figures/usermanagement/CES_UserManagement_Synchronisation_Groups_DE.png)

Ist der Account eines Nutzers / einer Nutzerin schon intern im Dogu angelegt worden, wird der interne Account mit dem externen CAS-Account verknüpft, wenn der interne Nutzername mit dem externen Nutzernamen übereinstimmt. Dabei werden *im Regelfall* die internen Daten im Dogu mit den Daten aus dem **User Management** überschrieben und der Account wird als externer Account gekennzeichnet.

Beachten Sie beim Anlegen von Accounts und Gruppen im **User Management**, dass geänderte oder neu angelegte Accounts, Gruppen und Gruppenzuordnungen **nicht direkt** in anderen Dogus bekannt sind, sondern **bei der nächsten Anmeldung der Nutzerin / des Nutzers im jeweiligen Dogu** bekannt gemacht werden.

### Mögliches Vorgehen bei der Rechtekonfiguration

Möchten Sie als Administrator:in eine neue Gruppe anlegen und diese in unterschiedlichen Dogus direkt konfigurieren, kann sich das im folgenden Schaubild dargestellte Vorgehen empfehlen.

 ![Vorgehen bei Rechtekonfiguration](figures/usermanagement/CES_UserManagement_example_de.png)

Mit Hilfe eines Testaccounts können Sie sich in die Dogus einloggen, in denen Sie die neu angelegte Gruppe konfigurieren wollen. Nachdem Sie die Gruppe mit Ihrem Account im Dogu konfiguriert haben, können Sie den Testaccount zusätzlich zum Testen der Konfiguration verwenden.
