#!/bin/sh
export DATABASE_SERVER="lenovo-server.local"
export DATABASE_PORT=54322
mvn compile quarkus:dev
