#!/bin/bash
export CCU2=`ping -c1 homematic-ccu2.Speedport_W723_V_Typ_A_1_01_022 | grep 'from' |  awk ' {print $4 } '`
DATE=`date`
echo start end-of-day at: $DATE
java -jar  $APP_DIR/iot2Batch.jar -c end-of-day
DATE=`date`
echo start export-calendar at: $DATE
java -jar  $APP_DIR/iot2Batch.jar -c export-calendar /backup/calendar.csv
DATE=`date`
echo start export-configuration at: $DATE
java -jar  $APP_DIR/iot2Batch.jar -c export-configuration /backup/configuration.csv
DATE=`date`
echo start export-protocol at: $DATE
java -jar  $APP_DIR/iot2Batch.jar -c export-protocol /backup/protocol.csv
DATE=`date`
echo start cleanup-calendar at: $DATE
java -jar  $APP_DIR/iot2Batch.jar -c cleanup-calendar
DATE=`date`
echo start cleanup-protocol at: $DATE
java -jar  $APP_DIR/iot2Batch.jar -c cleanup-protocol