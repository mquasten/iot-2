#!/bin/sh
DATE=`date`;
echo "Start h2 database at: $DATE";
java -cp ${APP_DIR}/h2*.jar org.h2.tools.Server -web -webAllowOthers -tcp -tcpAllowOthers -baseDir ${DATA_DIR} 2>&1 & 