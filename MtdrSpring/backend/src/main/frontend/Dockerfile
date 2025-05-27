# Imagen base con Node.js
FROM node:20-alpine

# Directorio de trabajo dentro del contenedor
WORKDIR /app

# Copiamos package.json y package-lock.json
COPY package*.json ./

# Instalamos dependencias
RUN npm install

# Copiamos el resto del proyecto
COPY . .

# Exponemos el puerto que usa Vite
EXPOSE 5173

# Comando para correr la app en modo desarrollo
CMD ["npm", "run", "dev", "--", "--host"]
