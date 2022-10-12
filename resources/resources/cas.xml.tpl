<?xml version="1.0" encoding="UTF-8"?>
<cas>
  <service>https://[{{ .GlobalConfig.Get "fqdn" }}]/usermgt/login/cas</service>
  <server-url>https://[{{ .GlobalConfig.Get "fqdn" }}]/cas</server-url>
  <failure-url>https://[{{ .GlobalConfig.Get "fqdn" }}]/usermgt/error/auth.html</failure-url>
  <login-url>https://[{{ .GlobalConfig.Get "fqdn" }}]/cas/login?service=https%3A%2F%2F%5Bfde4%3A8dba%3A82e1%3A%3Ac4%5D%2Fusermgt%2Flogin%2Fcas</login-url>
  <logout-url>https://[{{ .GlobalConfig.Get "fqdn" }}]/cas/logout</logout-url>
  <role-attribute-names>groups</role-attribute-names>
  <administrator-role>{{ .GlobalConfig.GetOrDefault "manager_group" "cesManager" }}</administrator-role>
</cas>
