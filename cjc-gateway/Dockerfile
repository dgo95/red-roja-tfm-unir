# Etapa 1: Compilar la aplicación
FROM maven:3.9.7-eclipse-temurin-17-alpine AS build
WORKDIR /app

# Copiar el archivo settings.xml personalizado, si lo necesitas
COPY settings.xml /root/.m2/settings.xml

# Copiar el archivo pom.xml y descargar las dependencias
COPY pom.xml .

# Copiar el código fuente de la aplicación
COPY src ./src

# Compilar la aplicación, omitiendo las pruebas para acelerar el proceso
RUN mvn clean package -DskipTests

# Etapa 2: Crear la imagen final para ejecución
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copiar el JAR compilado desde la etapa de build
COPY --from=build /app/target/cjc-gateway-*.jar app.jar

# Crear un usuario no root para mayor seguridad
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

# Exponer el puerto en el que la aplicación se ejecutará
EXPOSE 8443

# Comando por defecto para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]
