#!/bin/sh

docker rm -f moviesdb-dev || echo -ne ""
docker run --name moviesdb-dev -p 54322:5432 \
    -e POSTGRES_PASSWORD=movies0 -e POSTGRES_USER=movies \
    -e POSTGRES_DB=movies -d moviesdb-dev:9.4
