#!/usr/bin/env sh
set -eu

# If a generic database URL is provided, derive Spring datasource envs.
# Expected format: mysql://user:pass@host:port/dbname?reconnect=true
# Legacy provider-specific variables are kept as fallbacks for compatibility.
DB_URL="${DATABASE_URL:-${DB_URL:-${JAWSDB_URL:-${CLEARDB_DATABASE_URL:-}}}}"

if [ -n "${DB_URL}" ]; then
  proto_removed="${DB_URL#*://}"              # user:pass@host:port/dbname?...
  creds="${proto_removed%@*}"                 # user:pass
  host_and_path="${proto_removed#*@}"         # host:port/dbname?...
  user="${creds%%:*}"
  pass="${creds#*:}"
  host_port_path="${host_and_path%%\?*}"      # host:port/dbname
  raw_query=""
  if [ "${host_and_path#*\?}" != "${host_and_path}" ]; then
    raw_query="${host_and_path#*\?}"
  fi
  host_port="${host_port_path%%/*}"           # host:port
  dbname="${host_port_path#*/}"               # dbname
  host="${host_port%%:*}"
  port="${host_port#*:}"
  # もしポートが省略されている場合は 3306 を使用
  if [ "${port}" = "${host}" ]; then
    port=3306
  fi

  tls_query="sslMode=${DB_SSL_MODE:-VERIFY_IDENTITY}&enabledTLSProtocols=${DB_TLS_PROTOCOLS:-TLSv1.2,TLSv1.3}"
  default_query="${tls_query}&zeroDateTimeBehavior=CONVERT_TO_NULL&characterEncoding=UTF-8&useUnicode=true&serverTimezone=UTC"

  if [ -n "${raw_query}" ]; then
    jdbc_query="${raw_query}&${default_query}"
  else
    jdbc_query="${default_query}"
  fi

  # Build JDBC URL. Managed MySQL-compatible providers such as TiDB Cloud
  # require TLS, so do not force insecure transport here.
  export SPRING_DATASOURCE_URL="jdbc:mysql://${host}:${port}/${dbname}?${jdbc_query}"
  export SPRING_DATASOURCE_USERNAME="${user}"
  export SPRING_DATASOURCE_PASSWORD="${pass}"
fi

# Default active profile
export SPRING_PROFILES_ACTIVE="${SPRING_PROFILES_ACTIVE:-prod}"

# Ensure PORT is used by Spring Boot.
JAVA_OPTS="${JAVA_OPTS:-} -Dserver.port=${PORT:-8080}"

# Avoid spawning a login shell here. In the runtime image that can reset PATH and
# break `java` resolution even though the JRE is installed.
# shellcheck disable=SC2086
exec java ${JAVA_OPTS} -jar app.jar
