<?xml version="1.0" encoding="UTF-8"?>
<mail>
    <host>postfix</host>
    <port>25</port>
    <from>{{ .Config.GetOrDefault "mail/sender" "no-reply@cloudogu.com"}}</from>
    <subject>{{ .Config.GetOrDefault "mail/import/subject" "Ihr neuer Cloudogu Ecosystem Account"}}</subject>
    <message>{{ .Config.GetOrDefault "mail/import/message" "Willkommen im Cloudogu Ecosystem!\n\nDies ist Ihr Benutzeraccount:\n\nBenutzername: ${username}\nPasswort: ${password}\n\nBei der ersten Anmeldung m\u00FCssen Sie das Passwort \u00E4ndern."}}</message>
    <maxRetries>{{ .Config.GetOrDefault "mail/import/max_retries" "10"}}</maxRetries>
    <maxRetryDelay>{{ .Config.GetOrDefault "mail/import/max_retry_delay" "3600"}}</maxRetryDelay>
    <retryInterval>{{ .Config.GetOrDefault "mail/import/retry_interval" "30"}}</retryInterval>
</mail>
