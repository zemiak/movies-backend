#!/bin/sh

mvn package -q && java -jar -Dquarkus.profile=integrationtests target/quarkus-app/quarkus-run.jar
