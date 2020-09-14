#!/bin/sh

tag=quay.io/zemiak/movies-backend

mvn package -Dmaven.test.skip || exit 10
docker build . -f ./src/main/docker/Dockerfile.fast-jar -t ${tag} || exit 20
echo ${tag}
