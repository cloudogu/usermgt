#!/bin/sh
JAVA_OPTS="$JAVA_OPTS -Djava.awt.headless=true"
JAVA_OPTS="$JAVA_OPTS -Djava.net.preferIPv4Stack=true"
JAVA_OPTS="$JAVA_OPTS -Duniverseadm.stage=PRODUCTION"
JAVA_OPTS="$JAVA_OPTS -Duniverseadm.home=${UNIVERSEADM_HOME}"
JAVA_OPTS="$JAVA_OPTS -Djavax.net.ssl.trustStore=${TRUSTSTORE}"
JAVA_OPTS="$JAVA_OPTS -Djavax.net.ssl.trustStorePassword=changeit"
if [ "$(doguctl config "container_config/memory_limit" -d "empty")" != "empty" ];  then
  # Retrieve configurable java limits from etcd, valid default values exist
  MEMORY_LIMIT_MAX_PERCENTAGE=$(doguctl config "container_config/java_max_ram_percentage")
  MEMORY_LIMIT_MIN_PERCENTAGE=$(doguctl config "container_config/java_min_ram_percentage")

  echo "Setting memory limits to MaxRAMPercentage: ${MEMORY_LIMIT_MAX_PERCENTAGE} and MinRAMPercentage: ${MEMORY_LIMIT_MIN_PERCENTAGE}..."
  JAVA_OPTS="$JAVA_OPTS -XX:MaxRAMPercentage=${MEMORY_LIMIT_MAX_PERCENTAGE}"
  JAVA_OPTS="$JAVA_OPTS -XX:MinRAMPercentage=${MEMORY_LIMIT_MIN_PERCENTAGE}"
fi

ADMIN_GROUP="$(doguctl config --global --default "admin" "admin_group")"
export ADMIN_GROUP
CES_MANAGER_GROUP="$(doguctl config --global --default "cesManager" "manager_group")"
export CES_MANAGER_GROUP
