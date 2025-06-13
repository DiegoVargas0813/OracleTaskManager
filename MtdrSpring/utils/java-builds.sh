#!/bin/bash
# Copyright (c) 2021 Oracle and/or its affiliates.
# Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.

# Fail on error
set -e

BUILDS="todolistapp-springboot"

# Provision Repos (comentado por ahora)
# while ! state_done JAVA_REPOS; do
#   for b in $BUILDS; do
#     oci artifacts container repository create --compartment-id "$(state_get COMPARTMENT_OCID)" --display-name "$(state_get RUN_NAME)/$b" --is-public true
#   done
#   state_set_done JAVA_REPOS
# done

# Install Graal
while ! state_done GRAAL; do
  if ! test -d ~/graalvm-ce-java11-20.1.0; then
    echo "📥 Descargando GraalVM..."
    curl -sL https://github.com/graalvm/graalvm-ce-builds/releases/download/vm-20.1.0/graalvm-ce-java11-linux-amd64-20.1.0.tar.gz | tar xz
    mv graalvm-ce-java11-20.1.0 ~/
  fi
  state_set_done GRAAL
  echo "✅ GraalVM descargado y listo"
done

# Install GraalVM native-image
while ! state_done GRAAL_IMAGE; do
  echo "🛠️ Instalando native-image..."
  ~/graalvm-ce-java11-20.1.0/bin/gu install native-image
  state_set_done GRAAL_IMAGE
done

# Esperar login en OCIR
while ! state_done DOCKER_REGISTRY; do
  echo "⏳ Esperando login en Docker Registry..."
  sleep 5
done

echo "🏗️ Compilando el backend con Maven..."
cd "$MTDRWORKSHOP_LOCATION/backend"
mvn clean package -Pskip-frontend -Dmaven.multiModuleProjectDirectory="$MTDRWORKSHOP_LOCATION/backend"

# Definir la ruta al .jar generado
JAR_PATH="$MTDRWORKSHOP_LOCATION/backend/target/MyTodoList-0.0.1-SNAPSHOT.jar"

# Verificar existencia y copiar con nombre estándar
if [ -f "$JAR_PATH" ]; then
  echo "✅ JAR generado en: $JAR_PATH"
  cp "$JAR_PATH" "$MTDRWORKSHOP_LOCATION/backend/target/MyTodoList.jar"
  JAR_FINAL="$MTDRWORKSHOP_LOCATION/backend/target/MyTodoList.jar"
  echo "📦 Copiado como: $JAR_FINAL"
else
  echo "❌ ERROR: No se encontró el archivo JAR esperado en $JAR_PATH"
  exit 1
fi

state_set_done JAVA_BUILDS

# Build y deploy (comentado por ahora)
# while ! state_done JAVA_BUILDS; do
#   echo "🏗️ Construyendo imágenes..."
#   for b in $BUILDS; do
#     cd "$MTDRWORKSHOP_LOCATION/backend"
#     time ./build.sh &>> "$MTDRWORKSHOP_LOG/build-backend.log"
#   done
#   state_set_done JAVA_BUILDS
# done

# while ! state_done JAVA_DEPLOY; do
#   echo "📤 Subiendo imágenes..."
#   for b in $BUILDS; do
#     cd "$MTDRWORKSHOP_LOCATION/backend"
#     time ./deploy.sh &>> "$MTDRWORKSHOP_LOG/deploy-backend.log"
#   done
#   state_set_done JAVA_DEPLOY
# done


