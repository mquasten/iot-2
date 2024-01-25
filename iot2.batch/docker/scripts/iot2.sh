#!/bin/sh
export CCU2=`ping -c1 -4 homematic-ccu2.Speedport_W723_V_Typ_A_1_01_022 | grep 'from' |  awk ' {print $4 } ' | sed 's/://g'`
java -jar $APP_DIR/iot2Batch.jar $@