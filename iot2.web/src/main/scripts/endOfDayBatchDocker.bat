docker rm -f iot2  >NUL 2>&1

REM docker run  -d --name iot2 -p 8080:8080   -p 1522:1521  -v C:\iot/oracle:/opt/oracle/oradata  -v C:\iot/backup:/backup    iot2

docker run  -d --name iot2 -p 8080:8080  -p 9092:9092 -p 8082:8082 -v C:\iot/h2:/db -v C:\iot/backup:/backup   iot2

REM timeout 30 > NUL
docker logs  iot2  >> C:\iot/log/iot.log 2>>&1
docker exec -it iot2 endOfDay   >> C:\iot/log/iot.log 2>>&1
docker exec -it iot2 backup   >> C:\iot/log/iot.log 2>>&1
echo.  >> C:\iot/log/iot.log 2>>&1
docker stop iot2  >NUL 2>&1
docker rm -f iot2  >NUL 2>&1
docker system prune -f  >NUL 2>&1



