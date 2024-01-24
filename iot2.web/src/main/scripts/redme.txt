
mklink  iot.bat c:\iot\scripts\iotBatchDocker.bat
mklink  iot.bat c:\iot\scripts\endOfDayBatchDocker.bat

mklink  startWeb.bat c:\iot\scripts\startWebDocker.bat
mklink  stopWeb.bat c:\iot\scripts\stopWebDocker.bat

docker system prune -f  --all --volumes
docker load -i iot2.tar 