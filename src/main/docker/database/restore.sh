#!/bin/sh

set -e

zcat /tmp/dump.gz | psql --username "$POSTGRES_USER" --dbname "$POSTGRES_DB"
