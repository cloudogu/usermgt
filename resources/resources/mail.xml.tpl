<?xml version="1.0" encoding="UTF-8"?>
<mail>
    <host>postfix</host>
    <port>25</port>
    <from>{{ .Config.GetOrDefault "mail/sender" "no-reply@cloudogu.com"}}</from>
    <subject>{{ .Config.GetOrDefault "mail/import/subject" "Ihr neuer CES Account"}}</subject>
    <message>{{ .Config.GetOrDefault "mail/import/message" "<![CDATA[
        <html>
            <body>
                <h2>Willkommen zum Cloudogu Ecosystem!</h2>
                <p>Dies ist ihr Benutzeraccount</p>
                <p>Benutzername: ${username}</p>
                <p>Passwort: ${password}</p>
                <p>Bei der ersten Anmeldung müssen sie ihr Passwort ändern</p>
            </body>
        </html>]]>"}}
    </message>
</mail>
