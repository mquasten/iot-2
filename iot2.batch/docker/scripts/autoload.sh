#!/bin/sh
for script in $HOME/startup/*.sh
do
   if [ -x $script ]; then
      echo "execute $script";
      $script;
   fi;
done