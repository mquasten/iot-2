docker run  -v C:/iot/h2:/db  -v C:/iot/backup:/backup iot2 ! endOfDay.sh >> C:\iot/log/iot.log 2>>&1
echo start docker aufräumen: %DATE% %TIME%  >> C:\iot/log/iot.log 2>>&1
docker system prune -f