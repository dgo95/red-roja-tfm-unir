# Etapa 1: Compilar la aplicación
FROM maven:3.9.7-eclipse-temurin-17-alpine AS build
WORKDIR /app

# Copiar el archivo settings.xml personalizado
COPY settings.xml /root/.m2/settings.xml

# Copiar los archivos de configuración de Maven y las dependencias
COPY pom.xml .
COPY src ./src

# Compilar la aplicación, omitiendo las pruebas para acelerar el proceso
RUN mvn clean package -DskipTests

# Etapa 2: Crear la imagen final para ejecución
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copiar el JAR compilado desde la etapa anterior
COPY --from=build /app/target/*.jar app.jar

# Exponer el puerto en el que la aplicación se ejecutará
EXPOSE 8080

# Comando por defecto para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]