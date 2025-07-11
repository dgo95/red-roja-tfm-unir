#!/usr/bin/env sh
set -e

mc alias set local http://minio:9000 "$MINIO_ROOT_USER" "$MINIO_ROOT_PASSWORD"

for B in redroja-dev redroja-svc; do
  if ! mc ls "local/$B" >/dev/null 2>&1; then
    echo "⤵  Creando bucket $B con Object Lock…"
    mc mb --with-lock "local/$B"
  else
    echo "✓  Bucket $B ya existe"
  fi
done
