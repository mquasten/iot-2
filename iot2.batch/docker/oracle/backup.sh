#!/bin/sh
DATE=`date`
echo start database backup: $DATE
expdp iot2/iot2@XE  SCHEMAS=iot2 DIRECTORY=backup DUMPFILE=backup.dmp LOGFILE=backup.log  REUSE_DUMPFILES=Y