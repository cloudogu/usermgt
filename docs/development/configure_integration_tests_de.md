# Konfiguration für Integrations-Tests

Die Integrationstests erwarten eine bestimmte Konfiguration, damit diese erfolgreich durchlaufen. Konkret müssen
bestimmte Werte im etcd gesetzt sein. Dies sind folgende:

```bash
etcdctl set /config/_global/password-policy/must_contain_capital_letter true
etcdctl set /config/_global/password-policy/must_contain_lower_case_letter true
etcdctl set /config/_global/password-policy/must_contain_digit true
etcdctl set /config/_global/password-policy/must_contain_special_character true
etcdctl set /config/_global/password-policy/min_length 14
```

Damit die gesetzten Werte berücksichtigt werden, muss das Dogu einmal neu gestartet werden.

Die Werte konfigurieren die Passwort-Regeln, welche in den Integrationstests überprüft werden.

## Voraussetzungen

* Es ist notwendig, das Programm `yarn` zu installieren

## Konfiguration

Damit alle Integrationstests auch einwandfrei funktionieren, müssen vorher einige Daten konfiguriert werden.

**integrationTests/cypress.json** [[Link zur Datei](../../integrationTests/cypress.config.ts)] <!-- markdown-link-check-disable-line -->

1) Es muss die base-URL auf das Hostsystem angepasst werden.
   - Dafür muss das Feld `baseUrl` auf die Host-FQDN angepasst werden.
2) Es müssen noch weitere Aspekte konfiguriert werden.
   Diese werden als Umgebungsvariablen in der `cypress.json`, um den CES-Administrator im eigenen System auffindbar zu machen:
   - `DoguName` - Bestimmt den Namen des jetzigen Dogus und wir beim Routing benutzt.
   - `AdminUsername` - Der Benutzername des CES-Admins.
   - `AdminPassword` - Das Passwort des CES-Admins.
   - `AdminGroup` - Die Benutzergruppe für CES-Administratoren.

Eine Beispiel-`cypress.config.ts` sieht folgendermaßen aus:
```json
{
  "...": "...andere Werte...",
  "baseUrl": "https://192.168.56.2",
  "env": {
    "DoguName": "cas/login",
    "MaxLoginRetries": 3,
    "AdminUsername": "ces-admin",
    "AdminPassword": "ecosystem2016",
    "AdminGroup": "CesAdministrators" 
  }
}
```

## Starten der Integrationstests

Die Integrationstests können auf zwei Arten gestartet werden:

1. ggf. `yarn install` ausführen, damit Cypress vorhanden ist
2. Mit `yarn cypress run` starten die Tests nur in der Konsole ohne visuelles Feedback.
   Dieser Modus ist hilfreich, wenn die Ausführung im Vordergrund steht.
   Beispielsweise bei einer Jenkins-Pipeline.
3. Mit `yarn cypress open` startet ein interaktives Fenster, wo man die Tests ausführen, visuell beobachten und debuggen kann.
   Dieser Modus ist besonders hilfreich bei der Entwicklung neuer Tests und beim Finden von Fehlern.

Sollte in der Cypress-UI (`yarn cypress open`) ein Error basierend auf der `badeball/cypress-cucumber-preprocessor`-Bibliothek auftauchen und sich auf "Experimental Run All" bezieht, dann ist es ratsam, die Tests einzeln durchzuklicken. Dies kann ein Problem der Ausführungsmethodik sein, das nicht auf der Konsole (`yarn cypress run`) stattfindet.
