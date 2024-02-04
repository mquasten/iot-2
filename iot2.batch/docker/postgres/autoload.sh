#!/bin/sh
COUNTER=1
until [ $COUNTER -gt 500 ]
do
    psql -d iot -U iot2   -h localhost -c "select now()" > /dev/null 2>&1 
    if [ $? -eq 0 ]
    then
       echo "Connect to database iot as user iot2 successfull." 
       break;
    fi;
    sleep 2;
    COUNTER=$((COUNTER + 1));
done

for script in $HOME/startup/*.sh
do
   if [ -x $script ]; then
      echo "execute $script";
      $script;
   fi;
done