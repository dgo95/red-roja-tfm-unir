###############################################################################
# TEST RÁPIDO MySQL · TLS + X509                                              #
###############################################################################
$NET    = "red-roja_common-net"
$VOL    = "red-roja_mysql-certs"
$IMAGE  = "mysql:8"
$DBHOST = "mysql-db-nucleo"
$DBUSER = "root"
$DBPASS = "root"

function Show-Result ($label, $ok) {
    if ($ok) { Write-Host "[$label] ✅  OK"   -ForegroundColor Green }
    else      { Write-Host "[$label] ❌  FALLO" -ForegroundColor Red  }
}

Clear-Host

Write-Host "`n### TEST 1 · sin TLS (debe BLOQUEAR)`n" -ForegroundColor Cyan
docker run --rm --network $NET $IMAGE `
  mysql "-h" $DBHOST "-u$DBUSER" "-p$DBPASS" `
        --ssl-mode=DISABLED --connect-timeout=5 -e "SELECT 1;"
$exit = $LASTEXITCODE
Show-Result "Transporte inseguro bloqueado" ($exit -ne 0)

Write-Host "`n### TEST 2 · TLS + cert inventado (debe BLOQUEAR)`n" -ForegroundColor Cyan
docker run --rm -v ${PWD}:/tmp alpine sh -c "
  apk add --no-cache openssl >/dev/null &&
  openssl req -newkey rsa:2048 -nodes -days 1 \
    -subj '/CN=evil' -keyout /tmp/bogus.key -x509 -out /tmp/bogus.crt"
docker run --rm --network $NET -v ${VOL}:/certs -v ${PWD}:/tmp $IMAGE `
  mysql "-h" $DBHOST "-u$DBUSER" "-p$DBPASS" --ssl-mode=VERIFY_CA `
        --ssl-ca=/certs/ca-int.pem --ssl-cert=/tmp/bogus.crt --ssl-key=/tmp/bogus.key `
        -e "SELECT 1;"
$exit = $LASTEXITCODE
Show-Result "Cert ficticio bloqueado" ($exit -ne 0)
Remove-Item .\bogus.crt, .\bogus.key -Force

Write-Host "`n### TEST 3 · TLS + cert VÁLIDO (debe ACEPTAR)`n" -ForegroundColor Cyan
docker run --rm --network $NET -v ${VOL}:/certs $IMAGE `
  mysql "-h" $DBHOST "-u$DBUSER" "-p$DBPASS" --ssl-mode=VERIFY_CA `
        --ssl-ca=/certs/ca-int.pem `
        --ssl-cert=/certs/mysql-client.pem `
        --ssl-key=/certs/mysql-client-key.pem `
        -e "SELECT NOW();"
$exit = $LASTEXITCODE
Show-Result "Cert Vault aceptado" ($exit -eq 0)
