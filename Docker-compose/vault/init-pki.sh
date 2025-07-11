#!/bin/sh
set -euo pipefail
apk add --no-cache curl jq

#######################################################################
# 0.  Rutas:      pki_root (CA raíz)      · pki_int (CA intermedia)   #
#     Los .pem quedarán en el volumen /out que compartes con MySQL    #
#######################################################################

# ── CA RAÍZ ──────────────────────────────────────────────────────────
vault secrets enable -path=pki_root pki           || true
vault secrets tune -max-lease-ttl=87600h pki_root
vault write -field=certificate pki_root/root/generate/internal \
      common_name="RedRoja Root CA" ttl=87600h > /out/ca-root.pem

# ── INTERMEDIA ───────────────────────────────────────────────────────
vault secrets enable -path=pki_int pki            || true
vault secrets tune -max-lease-ttl=43800h pki_int

# 1. CSR
vault write -format=json pki_int/intermediate/generate/internal \
      common_name="RedRoja Intermediate CA" \
| jq -r '.data.csr' > /tmp/int.csr

# 2. Firma con la raíz
vault write -format=json pki_root/root/sign-intermediate \
      csr=@/tmp/int.csr format=pem_bundle ttl=43800h \
| jq -r '.data.certificate' > /tmp/int.pem

# 3. Activa la intermedia
vault write pki_int/intermediate/set-signed certificate=@/tmp/int.pem
cp /tmp/int.pem /out/ca-int.pem               # ← es la que verá MySQL

# ── ROLES (ya en la intermedia) ─────────────────────────────────────
vault write pki_int/roles/mysql-server \
      allowed_domains="mysql-db-nucleo" allow_subdomains=true \
	  allow_bare_domains=true \
      server_flag=true  client_flag=false ttl=8760h
vault write pki_int/roles/mysql-client \
      allow_any_name=true server_flag=false client_flag=true ttl=8760h

# ── CERTIFICADOS ─────────────────────────────────────────────────────
# servidor
vault write -format=json pki_int/issue/mysql-server \
      common_name="mysql-db-nucleo" \
| tee >(jq -r '.data.certificate'  > /out/mysql-server.pem) \
      >(jq -r '.data.private_key'  > /out/mysql-server-key.pem) >/dev/null
chown 999:999 /out/mysql-server*.pem   # UID/GID de 'mysql' en la imagen oficial de MySQL
chmod 400    /out/mysql-server-key.pem

# cliente (opcional)
vault write -format=json pki_int/issue/mysql-client \
      common_name="mysql-app" \
| tee >(jq -r '.data.certificate'  > /out/mysql-client.pem) \
      >(jq -r '.data.private_key'  > /out/mysql-client-key.pem) >/dev/null

echo 'PKI con intermedia lista ✔'