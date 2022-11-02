<?xml version="1.0" encoding="UTF-8"?>
<application-configuration>
<sender-mail>{{ .Config.GetOrDefault "sender_mail" "info@cloudogu.com" }}</sender-mail>
<admin-group>{{ .GlobalConfig.Get "admin_group" }}</admin-group>
<manager-group>{{ .GlobalConfig.GetOrDefault "manager_group" "cesManager" }}</manager-group>
<subject>{{ .Config.GetOrDefault "import/subject"  "Your new CES Account"}}</subject>
<content>{{ .Config.GetOrDefault "import/content" "Welcome to the Cloudogu Ecosystem!\nThis is your Useraccount\nUsername = %s\nPassword = %s\nAfter your first login, a password change is required"}}</content>
</application-configuration>