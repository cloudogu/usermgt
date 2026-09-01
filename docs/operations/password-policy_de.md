# Passwort Policy

Im etcd vom CES können bestimmte Regeln für die Passwörter definiert werden. Diese Regeln müssen im User Management beim
Setzen von Passwörtern eingehalten werden.

## Konfiguration der Passwort-Regeln in der globalen Konfiguration

Konkret kann konfiguriert werden, ob ein Passwort bestimmte Zeichen enthalten muss und welche Länge ein Passwort
mindestens haben muss.

Mit dem Wert `true` kann bei den folgenden Einträgen die jeweilige Regel aktiviert werden.

* `password-policy/must_contain_capital_letter` - gibt an, ob das Passwort mindestens einen
  Großbuchstaben enthalten muss
* `password-policy/must_contain_lower_case_letter` - gibt an, ob das Passwort mindestens einen
  Kleinbuchstaben enthalten muss
* `password-policy/must_contain_digit` - gibt an, ob das Passwort mindestens eine Ziffer enthalten muss
* `password-policy/must_contain_special_character` - gibt an, ob das Passwort mindestens ein
  Sonderzeichen enthalten muss

Bei den Großbuchstaben zählen die Umlaute `Ä`, `Ö` und `Ü` dazu, bei den Kleinbuchstaben die Umlaute `ä`, `ö` und `u`
sowie das `ß`. Als Sonderzeichen gelten alle Zeichen, die weder Großbuchstabe, Kleinbuchstabe noch Ziffer sind.

Die Mindestlänge des Passworts kann über den Eintrag `password-policy/min_length` konfiguriert werden.
Hier ist ein numerischer Integerwert einzutragen. Wird kein Wert angegeben oder ein Nicht-Integerwert gesetzt, ist die
Mindestlänge 1.

Die Werte werden nach einem Neustart vom CAS herangezogen.

Die Werte können über `kubectl edit configmap -n ecosystem global-config` konfiguriert werden.
