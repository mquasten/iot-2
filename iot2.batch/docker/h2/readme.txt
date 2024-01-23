Dockerfile
# startet eine Datenbank zu Testzwecken mit extermem Verzeichnis 

docker system prune -f  --all --volumes
docker build -t h2 .
docker run -d -p 9092:9092 -p 8082:8082 -v C:/mq/h2:/db  h2 

localhost/8082
Generic H2 (Server)
jdbc:h2:tcp://localhost/iot



DockerfileIot2H2
# docker image für iot2batch
docker system prune -f  --all --volumes
docker build -t iot2batchh2  -f docker/h2/DockerfileIot2H2 .
docker run -d --name iot2batchh2 -p 9092:9092 -p 8082:8082 -v C:\iot/h2:/db -v C:\iot/backup:/backup  iot2batchh2


docker exec -it iot2batchh2 sh
docker exec -it iot2batchh2 endOfDay[.sh]
docker exec -it iot2batchh2 iot2[.sh] -c end-of-day


docker save -o iot2batchOracle.tar  
docker load -i iot2batchOracle.tar 