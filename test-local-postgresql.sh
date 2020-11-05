#!/bin/sh

test -d ../database
if [ $? -ne 0 ]
then
    echo "$0 Database project is not checked out"
    exit 10
fi

curl 127.0.0.1:5432 2>/dev/null >/dev/null
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
mvn package
if [ $? -ne 0 ]
then
    echo "$0 error packaging main app"
    exit 30
fi

java -jar -Dquarkus.profile=integrationtests target/quarkus-app/quarkus-run.jar &
while true
do
    sleep 2s
    curl 127.0.0.1:8081 2>/dev/null >/dev/null
    if [ $? -eq 0 ]
    then
        break
    fi
done

cd movies-st
mvn clean package -q

killall java
