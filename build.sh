#!/bin/sh

mvn package -q && docker build . -t movies-backend:latest -f ./src/main/docker/Dockerfile.fast-jar -q
