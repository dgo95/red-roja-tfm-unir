#!/bin/bash
set -eu

echo "🔒 Configurando TDE para el schema '${MYSQL_DATABASE}'..."

mysql -u root -p"${MYSQL_ROOT_PASSWORD}" -e \
  "SET PERSIST default_table_encryption = 'ON';"

mysql -u root -p"${MYSQL_ROOT_PASSWORD}" -e \
  "ALTER DATABASE \`${MYSQL_DATABASE}\` ENCRYPTION='Y';"
echo "✅ TDE habilitado correctamente en el schema '${MYSQL_DATABASE}'."