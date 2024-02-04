docker stop iot2  >NUL 2>&1
docker rm   iot2 >NUL 2>&1
echo stop web-ui at: %DATE% %TIME%  >> C:\iot/log/iotWeb.log 2>>&1

