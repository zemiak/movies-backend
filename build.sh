#!/bin/sh
quarkusVersion="0.15.0"
appVersion="$(cat pom.xml | grep '<version>' | head -1 | cut -d'>' -f2 | cut -d'<' -f1)"
artifactId="$(cat pom.xml | grep '<artifactId>' | head -1 | cut -d'>' -f2 | cut -d'<' -f1)"

docker build --build-arg quarkusVersion=${quarkusVersion} --build-arg appVersion=${appVersion} \
    -t ${artifactId}:${appVersion} .
