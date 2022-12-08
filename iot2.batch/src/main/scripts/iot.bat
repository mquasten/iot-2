start java -jar C:\iot/h2/h2-2.1.214.jar -tcp -tcpAllowOthers  -baseDir C:\iot/h2/

echo start end-of-day at: %DATE% %TIME%  >> C:\iot/log/iot.log 2>>&1
java -jar C:\iot/iot2Batch.jar -c end-of-day >> C:\iot/log/iot.log 2>>&1

echo start cleanup at: %DATE% %TIME%  >> C:\iot/log/iot.log 2>>&1
java -jar C:\iot/iot2Batch.jar -c cleanup >> C:\iot/log/iot.log 2>>&1

echo start db backup at: %DATE% %TIME%  >> C:\iot/log/iot.log 2>>&1
java -cp "h2/h2-2.1.214.jar"  org.h2.tools.Script -url jdbc:h2:tcp://localhost/iot -user sa -password sa -script backup/backup.sql >> C:\iot/log/iot.log 2>>&1

shutdown /s /f /t 00 >>  C:\iot/log/iot.log 2>>&1