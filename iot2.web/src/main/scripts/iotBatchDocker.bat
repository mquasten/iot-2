echo start end-of-day at: %DATE% %TIME%  >> C:\iot/log/iot.log 2>>&1
docker  run  -v C:/iot/h2:/db  iot2  -c end-of-day >> C:\iot/log/iot.log 2>>&1

echo start export-calendar at: %DATE% %TIME%  >> C:\iot/log/iot.log 2>>&1
docker  run  -v C:/iot/h2:/db  -v C:/iot/backup:/backup iot2 -c export-calendar //backup/calendar.csv >> C:\iot/log/iot.log 2>>&1

echo start export-configuration at: %DATE% %TIME%  >> C:\iot/log/iot.log 2>>&1
docker  run  -v C:/iot/h2:/db  -v C:/iot/backup:/backup iot2 -c export-configuration //backup/configuration.csv >> C:\iot/log/iot.log 2>>&1

echo start export-protocol at: %DATE% %TIME%  >> C:\iot/log/iot.log 2>>&1
docker  run  -v C:/iot/h2:/db  -v C:/iot/backup:/backup iot2 -c export-protocol //backup/protocol.csv >> C:\iot/log/iot.log 2>>&1

echo start cleanup-calendar at: %DATE% %TIME%  >> C:\iot/log/iot.log 2>>&1
docker  run  -v C:/iot/h2:/db  iot2 -c cleanup-calendar >> C:\iot/log/iot.log 2>>&1

echo start cleanup-protocol at: %DATE% %TIME%  >> C:\iot/log/iot.log 2>>&1
docker  run  -v C:/iot/h2:/db  iot2  -c cleanup-protocol >> C:\iot/log/iot.log 2>>&1

echo start db backup at: %DATE% %TIME%  >> C:\iot/log/iot.log 2>>&1
docker  run  -v C:/iot/h2:/db  -v C:/iot/backup:/backup iot2 ! java -cp //h2/h2-2.1.214.jar  org.h2.tools.Script -url jdbc:h2:tcp://localhost/iot -user sa -password sa -script //backup/backup.sql >> C:\iot/log/iot.log 2>>&1

echo start docker aufräumen: %DATE% %TIME%  >> C:\iot/log/iot.log 2>>&1
docker system prune -f

REM shutdown /s /f /t 00 >>  C:\iot/log/iot.log 2>>&1

