#!/bin/bash
#fix 2
set -x

export IMAGE_NAME=todolistapp-springboot
export IMAGE_VERSION=0.2

java -version
mvn -version


if [ -z "$DOCKER_REGISTRY" ]; then
    export DOCKER_REGISTRY=$(state_get DOCKER_REGISTRY)
    echo "DOCKER_REGISTRY set."
fi
if [ -z "$DOCKER_REGISTRY" ]; then
    echo "Error: DOCKER_REGISTRY env variable needs to be set!"
    exit 1
fi

export IMAGE=${DOCKER_REGISTRY}/${IMAGE_NAME}:${IMAGE_VERSION}

# Always cd to the directory containing this script (which should be backend/)
cd "$(dirname "$0")"
echo "✅ Current directory: $(pwd)"
if [ ! -f pom.xml ]; then
    echo "❌ pom.xml not found in $(pwd)"
    exit 1
fi

PROJECT_ROOT=$(pwd)
echo "📁 Setting Maven multiModuleProjectDirectory to: $PROJECT_ROOT"
mvn -e clean package spring-boot:repackage -DskipTests -Dmaven.multiModuleProjectDirectory="$PROJECT_ROOT" \
    -Dfrontend-maven-plugin.systemNode=true -Dfrontend-maven-plugin.systemNpm=true


echo "🔍 Listing target directory after Maven build:"
ls -lh target
docker build -f Dockerfile -t $IMAGE .

docker push $IMAGE
if [  $? -eq 0 ]; then
    docker rmi "$IMAGE" #local
fi