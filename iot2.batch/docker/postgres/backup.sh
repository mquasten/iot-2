#!/bin/sh
DATE=`date`
echo "start database backup: $DATE"
pg_dump  -U iot2  --inserts iot -f /backup/backup.sql