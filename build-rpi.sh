#!/bin/sh

mvn clean package || exit 10
docker build . -f ./src/main/docker/Dockerfile-rpi -t movies
