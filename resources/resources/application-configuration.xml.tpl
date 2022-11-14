<?xml version="1.0" encoding="UTF-8"?>
<application-configuration>
    <sender-mail>{{ .Env.Get "MAIL_ADDRESS" }}</sender-mail>
    <admin-group>{{ .GlobalConfig.Get "admin_group" }}</admin-group>
    <manager-group>{{ .GlobalConfig.GetOrDefault "manager_group" "cesManager" }}</manager-group>
    <import-mail-subject>{{ .Config.GetOrDefault "import/mail/subject"  "Your new CES Account"}}</import-mail-subject>
    <import-mail-content>{{ .Config.GetOrDefault "import/mail/content" "Welcome to the Cloudogu Ecosystem!\nThis is your Useraccount\nUsername = %s\nPassword = %s\nAfter your first login, a password change is required"}}</import-mail-content>
    <port>25</port>
    <host>postfix</host>
</application-configuration>