#!/bin/sh
mysql  --protocol=TCP --user=iot2 --password=iot2 iot -e "SELECT now()"  > /dev/null 2>&1 ;
