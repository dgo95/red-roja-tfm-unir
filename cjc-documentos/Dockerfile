# -----------------------------------------
# Etapa 1: Compilar la aplicación
# -----------------------------------------
FROM maven:3.9.7-eclipse-temurin-17-alpine AS build

WORKDIR /app

# Copiar el settings.xml (si lo requieres)
COPY settings.xml /root/.m2/settings.xml

# Copiar pom.xml y el código fuente
COPY pom.xml .
COPY src ./src

# Compilar la aplicación, omitiendo las pruebas para acelerar el proceso
RUN mvn clean package -DskipTests

# -----------------------------------------
# Etapa 2: Crear la imagen final para ejecución
# -----------------------------------------
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copia el JAR compilado
COPY --from=build /app/target/*.jar app.jar

# Crea las carpetas necesarias: la carpeta "oficial" y otra donde pondremos los archivos "semilla"
RUN mkdir -p /app/data/fotosPerfil \
    && mkdir -p /app/data/documentos \
    && mkdir -p /app/default-data/fotosPerfil

# Copia los archivos por defecto a /app/default-data/fotosPerfil
COPY --from=build /app/src/main/resources/fotosPerfil /app/default-data/fotosPerfil

# Copia el script de arranque
COPY entrypoint.sh /app/entrypoint.sh
RUN chmod +x /app/entrypoint.sh

# Exponemos el puerto
EXPOSE 8080

# Usamos nuestro script como ENTRYPOINT
ENTRYPOINT ["/app/entrypoint.sh"]
