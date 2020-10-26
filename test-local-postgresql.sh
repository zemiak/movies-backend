#!/bin/sh

test -d ../database
if [ $? -ne 0 ]
then
    echo "$0 Database project is not checked out"
    exit 10
fi

curl 127.0.0.1:5432 2>/dev/null >/dev/mull
if [ $? -ne 52 ]
then
    echo "$0 Local postgresql is not running"
    exit 20
fi

cd ../database
sh restore-local.sh
if [ $? -ne 0 ]
then
    echo "$0 error restoring local database"
    exit 30
fi

cd ../movies-backend
mvn clean package -q
if [ $? -ne 0 ]
then
    echo "$0 error packaging main app"
    exit 30
fi

java -jar -Dquarkus.profile=integrationtests target/quarkus-app/quarkus-run.jar &

cd movies-st
mvn clean package -q

killall java
