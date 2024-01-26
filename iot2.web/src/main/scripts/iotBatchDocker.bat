docker rm -f iot2  >NUL 2>&1

docker run  -d --name iot2 -p 8080:8080   -p 1522:1521  -v C:\iot/oracle:/opt/oracle/oradata  -v C:\iot/backup:/backup    iot2
timeout 30 > NUL
REM docker run  -d --name iot2 -p 8080:8080  -p 9092:9092 -p 8082:8082 -v C:\iot/h2:/db -v C:\iot/backup:/backup   iot2

echo start end-of-day at: %DATE% %TIME%  >> C:\iot/log/iot.log 2>>&1
docker  exec -it iot2 iot2  -c end-of-day >> C:\iot/log/iot.log 2>>&1

echo start export-calendar at: %DATE% %TIME%  >> C:\iot/log/iot.log 2>>&1
docker  exec -it iot2 iot2 -c export-calendar //backup/calendar.csv >> C:\iot/log/iot.log 2>>&1

echo start export-configuration at: %DATE% %TIME%  >> C:\iot/log/iot.log 2>>&1
docker  exec -it iot2 iot2 -c export-configuration //backup/configuration.csv >> C:\iot/log/iot.log 2>>&1

echo start export-protocol at: %DATE% %TIME%  >> C:\iot/log/iot.log 2>>&1
docker  exec -it   iot2 iot2 -c export-protocol //backup/protocol.csv >> C:\iot/log/iot.log 2>>&1

echo start cleanup-calendar at: %DATE% %TIME%  >> C:\iot/log/iot.log 2>>&1
docker  exec -it  iot2  iot2 -c cleanup-calendar >> C:\iot/log/iot.log 2>>&1

echo start cleanup-protocol at: %DATE% %TIME%  >> C:\iot/log/iot.log 2>>&1
docker  exec -it  iot2  iot2  -c cleanup-protocol >> C:\iot/log/iot.log 2>>&1

docker exec -it iot2 backup   >> C:\iot/log/iot.log 2>>&1
echo.  >> C:\iot/log/iot.log 2>>&1

docker stop iot2  >NUL 2>&1
docker rm -f iot2  >NUL 2>&1
docker system prune -f  >NUL 2>&1

REM shutdown /s /f /t 00 >>  C:\iot/log/iot.log 2>>&1

