###########   Dockerfile   #################
# Etapa 1: build de Angular
############################################
FROM node:18-alpine AS builder

# Establece el directorio de trabajo
WORKDIR /app

# Copia los archivos de configuración de npm
COPY package*.json ./

# Instala las dependencias
RUN npm ci

# Copia el resto del código de la aplicación
COPY . .

# Construye la aplicación para producción
RUN npm run build -- --configuration=production

# Etapa 2: Servir la aplicación con Nginx
FROM nginx:stable-alpine

# 1- Quitamos el default.conf
RUN rm /etc/nginx/conf.d/default.conf

# 2- Copiamos nuestra conf (redirección HTTP→HTTPS incluida)
COPY nginx.conf /etc/nginx/conf.d/cjc.conf

# Copia los archivos construidos desde la etapa anterior
COPY --from=builder /app/dist/cjc-front/browser /usr/share/nginx/html

# Exponer el puerto en el que Nginx servirá la aplicación
EXPOSE 80 443

# Comando por defecto para iniciar Nginx
CMD ["nginx", "-g", "daemon off;"]
