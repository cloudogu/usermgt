<?xml version="1.0" encoding="UTF-8"?>
<cas>
  <service>https://{{ .GlobalConfig.Get "fqdn" }}/usermgt/login/cas</service>
  <server-url>https://{{ .GlobalConfig.Get "fqdn" }}/cas</server-url>
  <failure-url>https://{{ .GlobalConfig.Get "fqdn" }}/usermgt/error/auth.html</failure-url>
  <login-url>https://{{ .GlobalConfig.Get "fqdn" }}/cas/login?service=https://{{ .GlobalConfig.Get "fqdn" }}/usermgt/login/cas</login-url>
  <logout-url>https://{{ .GlobalConfig.Get "fqdn" }}/cas/logout</logout-url>
  <role-attribute-names>groups</role-attribute-names>
  <administrator-role>cesManager</administrator-role>
</cas>
