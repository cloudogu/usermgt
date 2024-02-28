<?xml version="1.0" encoding="UTF-8"?>
<mail>
    <host>postfix</host>
    <port>25</port>
    <from>{{ .Config.GetOrDefault "mail/sender" "no-reply@cloudogu.com"}}</from>
    <subject>{{ .Config.GetOrDefault "mail/import/subject" "Ihr neuer CES Account"}}</subject>
    <message>{{ .Config.GetOrDefault "mail/import/message" "Willkommen zum Cloudogu Ecosystem!\\n\\nDies ist ihr Benutzeraccount\\nBenutzername = ${username}\\nPasswort = ${password}\\n\\nBei der ersten Anmeldung müssen sie ihr Passwort ändern"}}</message>
</mail>
