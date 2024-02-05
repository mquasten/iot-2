#!/bin/sh
DATE=`date`
echo start import-calendar: $DATE
java -jar  $APP_DIR/iot2Batch.jar -c import-calendar ${APP_DIR}/calendar.csv
DATE=`date`
echo start import-configuration: $DATE
java -jar  $APP_DIR/iot2Batch.jar -c import-configuration ${APP_DIR}/configuration.csv
DATE=`date`
echo start create-user: $DATE
java -jar  $APP_DIR/iot2Batch.jar -c update-user mquasten manfred01 MD5

rm /tmp/spring.log