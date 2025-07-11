#!/usr/bin/env bash
set -euo pipefail

echo "🔧 Creando usuarios JPA y Flyway para schema '${MYSQL_DATABASE}'…"

mysql -u root -p"${MYSQL_ROOT_PASSWORD}" <<-EOSQL
  -- Usuario JPA
  CREATE USER IF NOT EXISTS '${MYSQL_JPA_USER}'@'%' IDENTIFIED BY '${MYSQL_JPA_PASSWORD}';
  GRANT ALL PRIVILEGES ON \`${MYSQL_DATABASE}\`.* TO '${MYSQL_JPA_USER}'@'%';

  -- Usuario Flyway
  CREATE USER IF NOT EXISTS '${MYSQL_FLYWAY_USER}'@'%' IDENTIFIED BY '${MYSQL_FLYWAY_PASSWORD}';
  GRANT ALL PRIVILEGES ON \`${MYSQL_DATABASE}\`.* TO '${MYSQL_FLYWAY_USER}'@'%';
EOSQL

if [[ "${ENTORNO:-}" != "pro" ]]; then
  echo "🌱 Entorno '${ENTORNO}', ajustando permisos de root@% para uso local…"
  mysql -u root -p"${MYSQL_ROOT_PASSWORD}" <<-EOSQL
    -- Desactiva el requisito X509 para root@%
    ALTER USER 'root'@'%' REQUIRE NONE;
    GRANT ALL PRIVILEGES ON *.* TO 'root'@'%' WITH GRANT OPTION;
EOSQL
fi

mysql -u root -p"${MYSQL_ROOT_PASSWORD}" -e "FLUSH PRIVILEGES;"

echo "✅ Usuarios creados y privilegios aplicados."
