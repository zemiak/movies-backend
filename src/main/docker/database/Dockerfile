FROM postgres:9.4

COPY movies-20190520.plain.gz /tmp/dump.gz
COPY restore.sh /docker-entrypoint-initdb.d/
