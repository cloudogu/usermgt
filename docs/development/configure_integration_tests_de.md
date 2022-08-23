# Konfiguration für Integrations-Tests

Die Integrationstests erwarten eine bestimmte Konfiguration, damit diese erfolgreich durchlaufen. Konkret müssen
bestimmte Werte im etcd gesetzt sein. Dies sind folgende:

```
etcdctl set /config/_global/password-policy/must_contain_capital_letter true
etcdctl set /config/_global/password-policy/must_contain_lower_case_letter true
etcdctl set /config/_global/password-policy/must_contain_digit true
etcdctl set /config/_global/password-policy/must_contain_special_character true
etcdctl set /config/_global/password-policy/min_length 14
```

Damit die gesetzten Werte berücksichtigt werden, muss das Dogu einmal neu gestartet werden.

Die Werte konfigurieren die Passwort-Regeln, welche in den Integrationstests überprüft werden.