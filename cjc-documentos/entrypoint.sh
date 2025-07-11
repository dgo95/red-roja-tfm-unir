#!/bin/sh
set -e

echo "=== Contenido de /app/default-data/fotosPerfil antes de la copia: ==="
ls -lAh /app/default-data/fotosPerfil || echo "No se encontró /app/default-data/fotosPerfil o está vacía."

echo "=== Contenido de /app/data/fotosPerfil antes de la copia: ==="
ls -lAh /app/data/fotosPerfil || echo "No se encontró /app/data/fotosPerfil o está vacía."

echo "=== Copiando archivos por defecto a /app/data/fotosPerfil (sin sobreescribir archivos existentes)... ==="
cp -Rn /app/default-data/fotosPerfil/* /app/data/fotosPerfil/ || true

echo "=== Contenido de /app/data/fotosPerfil después de la copia: ==="
ls -lAh /app/data/fotosPerfil || echo "No se encontró /app/data/fotosPerfil o está vacía."

echo "=== Eliminando la carpeta /app/default-data... ==="
rm -rf /app/default-data

if [ -d /app/default-data ]; then
  echo "La carpeta /app/default-data todavía existe (error inesperado)."
else
  echo "La carpeta /app/default-data ha sido eliminada con éxito."
fi

echo "=== Iniciando aplicación... ==="
# Limpiar la terminal antes de iniciar la aplicación
echo "=== Limpiando terminal antes de iniciar la aplicación... ==="
clear
exec java -jar app.jar
