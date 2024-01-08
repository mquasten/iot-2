echo start end-of-day at: %DATE% %TIME%  >> C:\iot/log/iot.log 2>>&1
docker run -p 8080:8080 -p 8082:8082  -v C:/mq/h2:/db  iot2 ! iotWeb.sh >> C:\iot/log/iot.log 2>>&1
echo start docker aufräumen: %DATE% %TIME%  >> C:\iot/log/iot.log 2>>&1
docker system prune -f