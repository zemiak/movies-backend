#!/bin/sh
export DATABASE_SERVER="spot-it.test.cp.local"
export DATABASE_PORT=54322
mvn compile quarkus:dev
