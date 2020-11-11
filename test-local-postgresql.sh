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

echo Restoring database...
cd ../database
sh restore-local.sh >/dev/null
if [ $? -ne 0 ]
then
    echo "$0 error restoring local database"
    exit 30
fi

echo Packaging the app...
cd ../movies-backend
mvn package -q
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
mvn package -q

killall java
