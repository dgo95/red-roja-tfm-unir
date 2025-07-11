<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8"/>
  <title>Activación de Cuenta</title>
  <style>
    body{font-family:Arial,sans-serif;background:#f4f4f4;margin:0;padding:0}
    .container{max-width:600px;margin:0 auto;background:#fff;padding:20px;
               border-radius:8px;box-shadow:0 2px 4px rgba(0,0,0,.1)}
    .btn{display:inline-block;background:#ff0000;color:#fff;text-decoration:none;
         padding:10px 20px;border-radius:5px;margin-top:20px}
    .btn:hover{background:#b30000}
    .footer,.signature{text-align:center;color:#999;margin-top:20px}
  </style>
</head>
<body>
  <div class="container">
    <div class="header"><h2>¡Bienvenido, ${user.firstName!}!</h2></div>

    <p>Tu cuenta ha sido creada correctamente en RedRoja.
       Haz clic para activarla:</p>

    <p><a class="btn" href="${link}">Activa tu cuenta</a></p>

    <p>Si el botón falla, copia y pega esta URL:<br/>${link}</p>

    <div class="footer">
      Gracias,<br/>El ${realm.displayName!}
    </div>

    <div class="signature">
      <b>¡NO HAY TEORÍA REVOLUCIONARIA SIN PRÁCTICA REVOLUCIONARIA!</b><br/>
      <img src="${url.resourcesPath}/img/principal.png" alt="logo" width="90" height="96"/><br/>
      <b>CJC&nbsp;-&nbsp;${realm.displayName!}</b>
    </div>
  </div>
</body>
</html>
