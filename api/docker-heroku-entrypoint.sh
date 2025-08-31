#!/usr/bin/env sh
set -eu

# If JAWSDB_URL or CLEARDB_DATABASE_URL is provided, derive Spring datasource envs.
# Expected format: mysql://user:pass@host:port/dbname?reconnect=true
DB_URL="${JAWSDB_URL:-${CLEARDB_DATABASE_URL:-}}"

if [ -n "${DB_URL}" ]; then
  proto_removed="${DB_URL#*://}"              # user:pass@host:port/dbname?...
  creds="${proto_removed%@*}"                 # user:pass
  host_and_path="${proto_removed#*@}"         # host:port/dbname?...
  user="${creds%%:*}"
  pass="${creds#*:}"
  host_port_path="${host_and_path%%\?*}"      # host:port/dbname
  host_port="${host_port_path%%/*}"           # host:port
  dbname="${host_port_path#*/}"               # dbname
  host="${host_port%%:*}"
  port="${host_port#*:}"
  # もしポートが省略されている場合は 3306 を使用
  if [ "${port}" = "${host}" ]; then
    port=3306
  fi
  # Build JDBC URL
  export SPRING_DATASOURCE_URL="jdbc:mysql://${host}:${port}/${dbname}?useSSL=false&allowPublicKeyRetrieval=true&zeroDateTimeBehavior=CONVERT_TO_NULL&characterEncoding=UTF-8&useUnicode=true&serverTimezone=UTC"
  export SPRING_DATASOURCE_USERNAME="${user}"
  export SPRING_DATASOURCE_PASSWORD="${pass}"
fi

# Default active profile
export SPRING_PROFILES_ACTIVE="${SPRING_PROFILES_ACTIVE:-prod}"

# Ensure PORT is used by Spring Boot
JAVA_OPTS="${JAVA_OPTS:-} -Dserver.port=${PORT:-8080}"

exec sh -lc "java ${JAVA_OPTS} -jar app.jar"
