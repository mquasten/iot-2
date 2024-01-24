#!/bin/sh
DATE=`date`
echo "start database backup: $DATE"
java -cp ${APP_DIR}/h2*.jar  org.h2.tools.Script -url jdbc:h2:tcp://localhost/iot -user sa -password sa -script /backup/backup.sql