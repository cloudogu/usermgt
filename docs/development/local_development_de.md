# Lokale Entwicklung

Die Oberfläche des User-Managements kann lokal gestartet werden.

## Mit Mock-Backend

### Dependencies installieren
```
yarn global add json-server
```

### (Mock)-Backend starten
```
cd app/src/main/ui
yarn backend
```

### Frontend starten
```
cd app/src/main/ui
yarn install
yarn dev
```

## Mit Dogu-Backend aus lokalem CES

### Patch-Datei einspielen

```shell
git apply docs/development/local_development_settings.patch
```

### Usermgt im CES neu bauen

```shell
cesapp build /vagrant/containers/usermgt
```

Diese Änderungen dürfen **nicht** eingecheckt werden und dienen lediglich dem Zweck, das lokale
Frontend mit einem realen Backend zu verbinden. Dies verringert den Testaufbau, da die restliche 
Infrastruktur bereits im CES vorhanden ist.

## Generierte Testdaten einspielen

- Nutzer anlegen: `create_users.py <Nutzeranzahl>`

Wird das Skript ohne Parameter aufgerufen, werden 5 Benutzer angelegt. Die Zählung beginnt immer bei 0.
Tritt ein Datenkonflikt auf, wird das Skript trotzdem fortgeführt.

Beispiel: 10 Nutzer anlegen
```shell
docs/development/create_users.py 10
```

- Gruppen anlegen: `create_groups.py <Gruppenanzahl>`

Wird das Skript ohne Parameter aufgerufen, werden 5 Benutzer angelegt. Die Zählung beginnt immer bei 0.
Tritt ein Datenkonflikt auf, wird das Skript trotzdem fortgeführt.

Beispiel: 10 Gruppen anlegen
```shell
docs/development/create_groups.py 10
```
