#!/bin/bash

# Ensure we are in the backend directory (where this script and Dockerfile are)
cd "$(dirname "$0")"

export IMAGE_NAME=todolistapp-springboot
export IMAGE_VERSION=0.2


if [ -z "$DOCKER_REGISTRY" ]; then
    export DOCKER_REGISTRY=$(state_get DOCKER_REGISTRY)
    echo "DOCKER_REGISTRY set."
fi
if [ -z "$DOCKER_REGISTRY" ]; then
    echo "Error: DOCKER_REGISTRY env variable needs to be set!"
    exit 1
fi

export IMAGE=${DOCKER_REGISTRY}/${IMAGE_NAME}:${IMAGE_VERSION}

mvn clean package -DskipTests
echo "🔍 Listing target directory after Maven build:"
ls -lh target
docker build -f Dockerfile -t $IMAGE .

docker push $IMAGE
if [  $? -eq 0 ]; then
    docker rmi "$IMAGE" #local
fi