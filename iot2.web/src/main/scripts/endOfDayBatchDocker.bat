docker rm -f iot2  >NUL 2>&1

docker run  -d --name iot2 -p 8080:8080   -p 1522:1521  -v C:\iot/oracle:/opt/oracle/oradata  -v C:\iot/backup:/backup    iot2

docker logs  iot2  >> C:\iot/log/iot.log 2>>&1
timeout 30 > NUL
docker exec -it iot2 endOfDay   >> C:\iot/log/iot.log 2>>&1
docker stop iot2  >NUL 2>&1
docker rm -f iot2  >NUL 2>&1
docker system prune -f  >NUL 2>&1



REM docker run  -v C:/iot/h2:/db  -v C:/iot/backup:/backup iot2 ! endOfDay.sh >> C:\iot/log/iot.log 2>>&1
REM echo start docker aufräumen: %DATE% %TIME%  >> C:\iot/log/iot.log 2>>&1
REM  docker system prune -f