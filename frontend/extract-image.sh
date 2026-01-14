#!/bin/bash
set -e

DATE="$(date +%Y-%m-%d-%H-%M)"
IMAGE_TAG="localhost/oskari-tre-frontend:${DATE}"

docker build --cpuset-cpus="0-3" . --tag "${IMAGE_TAG}"
id=$(docker create "${IMAGE_TAG}")
docker cp "$id:/opt/oskari/dist" - | gzip > "./oskari-frontend-${DATE}.tar.gz" || rm "./oskari-frontend-${DATE}.tar.gz"
echo "Removing container ${id}"
docker rm -v "${id}"
#docker image rm "${IMAGE_TAG}"
