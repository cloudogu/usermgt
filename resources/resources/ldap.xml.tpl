<?xml version="1.0" encoding="UTF-8"?>
<ldap>
  <host>ldap</host>
  <port>389</port>
  <bind-dn>{{ .Config.GetAndDecrypt "sa-ldap/username" }}</bind-dn>
  <bind-password>{{ .Env.Get "LDAP_BIND_PASSWORD" }}</bind-password>
  <user-base-dn>ou=People,o={{ .GlobalConfig.Get "domain" }},dc=cloudogu,dc=com</user-base-dn>
  <group-base-dn>ou=Groups,o={{ .GlobalConfig.Get "domain" }},dc=cloudogu,dc=com</group-base-dn>
  <disabled>false</disabled>
</ldap>
