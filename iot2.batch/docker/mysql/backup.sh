#!/bin/sh
DATE=`date`
echo "start database backup: $DATE"
mysqldump --compact  --user=root --password=mysql   --result-file=/backup/backup.sql   iot 