<?xml version="1.0" encoding="UTF-8"?>
<ldap>
  <host>{{ .Env.Get "LDAP_HOST" }}</host>
  <port>{{ .Env.Get "LDAP_PORT" }}</port>
  <bind-dn>{{ .Env.Get "LDAP_BIND_USER" }}</bind-dn>
  <bind-password>{{ .Env.Get "LDAP_BIND_PASSWORD_ENC" }}</bind-password>
  {{ if ne (.Env.Get "LDAP_GROUP_BASE_DN") "" }}
  <group-base-dn>{{ .Env.Get "LDAP_GROUP_BASE_DN" }}</group-base-dn>
  {{ else }}
  <group-base-dn>ou=Groups,o={{ .GlobalConfig.Get "domain" }},dc=cloudogu,dc=com</group-base-dn>
  {{ end }}
  {{ if ne (.Env.Get "LDAP_USER_BASE_DN") "" }}
  <user-base-dn>{{ .Env.Get "LDAP_USER_BASE_DN" }}</user-base-dn>
  {{ else }}
  <user-base-dn>ou=People,o={{ .GlobalConfig.Get "domain" }},dc=cloudogu,dc=com</user-base-dn>
  {{ end }}
  <disabled>false</disabled>
</ldap>
