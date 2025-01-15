### Benutzer synchron anstatt asynchron erzeugen
Benutzer können asynchron anstatt synchron erzeugt werden, indem der sync-Queryparameter angegeben wird. Dies verhindert korrupte LDAP-Zustände, ist aber wesentlich langsamer als synchrone Erzeugung.
Beispiel:

`curl -U user:password https://domain/usermgt/api/users?sync=true` 
