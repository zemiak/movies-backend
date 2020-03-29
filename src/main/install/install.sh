#!/bin/sh

svc=movies

which systemctl
if [ $? -eq 0 ]
then
    cp ./${svc}.service /etc/systemd/system/ || exit 10
    systemctl daemon-reload || exit 20
    systemctl enable ${svc}.service || exit 30
    systemctl stop ${svc}.service
    systemctl start ${svc}.service || exit 40
else
    cp ./${svc}.init /etc/init.d/${svc} || exit 10
    chmod +x /etc/init.d/${svc}
    /sbin/chkconfig --add ${svc} || exit 20
    /sbin/service ${svc} stop
    /sbin/service ${svc} start || exit 30
fi

cp movies-update /etc/cron.daily/ || exit 30
chmod +x /etc/cron.daily/movies-update

cp ../cron-moviethumbnails/movies-moviethumbnails /etc/cron.daily/ || exit 40
chmod +x /etc/cron.daily/movies-moviethumbnails
