echo start docker aufräumen: %DATE% %TIME%  >> C:\iot/log/iotWeb.log 2>>&1
docker system prune -f
echo start web-ui at: %DATE% %TIME%  >> C:\iot/log/iotWeb.log 2>>&1
start docker run -d -p 8080:8080 -p 8082:8082  -v C:/mq/h2:/db  iot2 ! iotWeb.sh