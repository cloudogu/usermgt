# Passwort Policy

Im etcd vom CES können bestimmte Regeln für die Passwörter definiert werden. Diese Regeln müssen im User Management beim
Setzen von Passwörtern eingehalten werden.

## Konfiguration der Passwort-Regeln im etcd

Konkret kann konfiguriert werden, ob ein Passwort bestimmte Zeichen enthalten muss und welche Länge ein Passwort
mindestens haben muss.

Mit dem Wert `true` kann bei den folgenden Einträgen die jeweilige Regel aktiviert werden.

* `/config/_global/password-policy/must_contain_capital_letter` - gibt an, ob das Passwort mindestens einen
  Großbuchstaben enthalten muss
* `/config/_global/password-policy/must_contain_lower_case_letter` - gibt an, ob das Passwort mindestens einen
  Kleinbuchstaben enthalten muss
* `/config/_global/password-policy/must_contain_digit` - gibt an, ob das Passwort mindestens eine Ziffer enthalten muss
* `/config/_global/password-policy/must_contain_special_character` - gibt an, ob das Passwort mindestens ein
  Sonderzeichen enthalten muss

Bei den Großbuchstaben zählen die Umlaute `Ä`, `Ö` und `Ü` dazu, bei den Kleinbuchstaben die Umlaute `ä`, `ö` und `u`
sowie das `ß`. Als Sonderzeichen gelten alle Zeichen, die weder Großbuchstabe, Kleinbuchstabe noch Ziffer sind.

Die Mindestlänge des Passworts kann über den Eintrag `/config/_global/password-policy/min_length` konfiguriert werden.
Hier ist ein numerischer Integerwert einzutragen. Wird kein Wert angegeben oder ein Nicht-Integerwert gesetzt, ist die
Mindestlänge 1.

Die Werte werden nach einem Neustart vom CAS herangezogen.

Es ist zu beachten, dass diese Werte nicht über `cesapp edit-config usermgt` konfiguriert werden können, da es sich
hierbei um globale Werte handelt. Diese Werte sind für das gesamte CES gültig und somit nicht Dogu-spezifisch.