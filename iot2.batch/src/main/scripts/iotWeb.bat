start java -jar C:\iot/lib/h2-2.1.214.jar -tcp -tcpAllowOthers  -baseDir C:\iot/h2/

echo start web-ui at: %DATE% %TIME%  >> C:\iot/log/iotWeb.log 2>>&1
java -jar C:\iot/lib/iot2web.jar >> C:\iot/log/iotWeb.log 2>>&1

