<#
.SYNOPSIS
  Prueba local de límite de peticiones (DoS) contra el microservicio de documentos.
.DESCRIPTION
  1. Pide un access-token a Keycloak.
  2. Lanza 40 subidas del archivo test.txt.
  3. Muestra la lista de códigos HTTP (201 / 429).
#>
### ───────── 1. Configuración hard-codeada ─────────────────────────────────────
$User      = '0000'
$Password  = 'admin'
$ClientId  = 'redroja-frontend'
$Realm     = 'redroja-realm'
$TokenUrl  = "http://localhost:8081/realms/$Realm/protocol/openid-connect/token"

$FilePath  = 'C:\Users\Usuario\Desktop\test.txt'
$UploadUrl = 'http://localhost:8080/api/documentos/v1/archivos'
### ───────── 2. Obtener el access-token ───────────────────────────────────────
$body = @{
    grant_type = 'password'
    client_id  = $ClientId
    username   = $User
    password   = $Password
}
$tokenResponse = Invoke-RestMethod `
    -Method Post `
    -Uri    $TokenUrl `
    -Body   $body `
    -ContentType 'application/x-www-form-urlencoded'

$AccessToken = $tokenResponse.access_token
$HeaderAuth  = "Authorization: Bearer $AccessToken"

Write-Host "Token obtenido (longitud $($AccessToken.Length))" -ForegroundColor Green
### ───────── 3. Función inline para enviar 1 archivo ──────────────────────────
function Invoke-Upload {
    param($P,$U,$H)
    curl.exe -s -o NUL -w "%{http_code}`n" `
       -H $H `
       -F "archivo=@$P" `
       -F "titulo=test.txt" `
       -F "fecha=2025-07-04" `
       -F "confidencialidad=INTERNO" `
       -F "tipo=ESTATUTO" `
       -F "categoria=AGITACION" `
       -F "propietario=B2" `
       $U
}
### ───────── 4. Ráfaga de 30 peticiones (~3 s) ────────────────────────────────
Write-Host "`Lanzando 40 peticiones (espera…)`n" -ForegroundColor Cyan
1..40 | ForEach-Object {
    Start-Job -ScriptBlock ${function:Invoke-Upload} `
              -ArgumentList $FilePath,$UploadUrl,$HeaderAuth
} | Wait-Job | Receive-Job |
### ───────── 5. Resumen ───────────────────────────────────────────────────────
Group-Object |
ForEach-Object { "{0}  x{1}" -f $_.Name,$_.Count }