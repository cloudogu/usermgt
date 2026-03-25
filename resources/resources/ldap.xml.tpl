<?xml version="1.0" encoding="UTF-8"?>
<ldap>
  <host>ldap</host>
  <port>389</port>
  <bind-dn>{{ .Env.Get "LDAP_BIND_USER" }}</bind-dn>
  <bind-password>{{ .Env.Get "LDAP_BIND_PASSWORD_ENC" }}</bind-password>
  <user-base-dn>ou=People,o={{ .GlobalConfig.Get "domain" }},dc=cloudogu,dc=com</user-base-dn>
  <group-base-dn>ou=Groups,o={{ .GlobalConfig.Get "domain" }},dc=cloudogu,dc=com</group-base-dn>
  <disabled>false</disabled>
</ldap>
