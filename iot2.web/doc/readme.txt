docker system prune -f  --all --volumes
docker build -t iot2web .


docker run -d -p 8080:8080 -p 8082:8082  -v C:/mq/h2:/db  -v C:/mq/h2/backup:/backup iot2web  ! iotWeb.sh


docker run  -v C:/mq/h2:/db  -v C:/mq/h2/backup:/backup iot2web  update-user jcmaxwell divB=0 MD5