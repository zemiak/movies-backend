#!/bin/sh
quarkusVersion="$(sh quarkusVersion.sh)"
appVersion="$(sh appVersion.sh)"
docker build --build-arg quarkusVersion=${quarkusVersion} --build-arg appVersion=${appVersion} \
    -t movies-backend:${appVersion} .
