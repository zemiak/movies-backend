#!/bin/sh

svc=movies-backend

which systemctl
if [ $? -ne 0 ]
then
    echo systemd system needed
    exit 5
fi

cp ./${svc}.service /etc/systemd/system/ || exit 10
systemctl daemon-reload || exit 20
systemctl enable ${svc}.service || exit 30
systemctl stop ${svc}.service
systemctl start ${svc}.service || exit 40
