<?xml version="1.0" encoding="UTF-8"?>
<application-configuration>
    <admin-group>{{ .GlobalConfig.Get "admin_group" }}</admin-group>
    <manager-group>{{ .GlobalConfig.GetOrDefault "manager_group" "cesManager" }}</manager-group>
</application-configuration>
