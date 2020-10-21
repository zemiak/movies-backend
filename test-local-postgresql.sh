#!/bin/sh

cp src/main/resources/application.properties src/main/resources/application-main.properties
cp src/main/resources/application-prodtest.properties src/main/resources/application.properties
mvn clean test
cp src/main/resources/application-main.properties src/main/resources/application.properties && rm -f src/main/resources/application-main.properties
