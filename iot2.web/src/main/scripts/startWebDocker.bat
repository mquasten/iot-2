echo start docker aufräumen: %DATE% %TIME%  >> C:\iot/log/iotWeb.log 2>>&1
docker system prune -f
docker rm -f  iot2  >NUL 2>&1
echo start web-ui at: %DATE% %TIME%  >> C:\iot/log/iotWeb.log 2>>&1
docker run --name iot2 -d -p 8080:8080 -p 8082:8082 -p 9092:9092  -v C:/iot/h2:/db  iot2 ! iotWeb.sh 
docker logs -f  iot2  >> C:\iot/log/iotWeb.log 2>>&1