#!/bin/bash
DATE=`date`
echo start import-calendar: $DATE
java -jar  $APP_DIR/iot2Batch.jar -c import-calendar /opt/oracle/scripts/setup/calendar.csv
DATE=`date`
echo start import-configuration: $DATE
java -jar  $APP_DIR/iot2Batch.jar -c import-configuration /opt/oracle/scripts/setup/configuration.csv
DATE=`date`
echo start create-user: $DATE
java -jar  $APP_DIR/iot2Batch.jar -c update-user mquasten manfred01 MD5