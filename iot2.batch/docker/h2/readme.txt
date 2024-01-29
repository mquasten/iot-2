Dockerfile
# startet eine Datenbank zu Testzwecken mit extermem Verzeichnis 

docker system prune -f  --all --volumes
docker build -t h2 -f docker/h2/Dockerfile .
docker run  -d  --name h2 -p 9092:9092 -p 8082:8082 -v C:/mq/h2:/db  h2 

localhost/8082
Generic H2 (Server)
jdbc:h2:tcp://localhost/iot



Dockerfile: DockerfileIot2Alpine
# docker image für iot2batch Alpine + Jave Runtime mit  musl libc  (without  glibc and friends)
docker system prune -f  --all --volumes
docker build -t iot2batch  -f docker/h2/DockerfileIot2Alpine .
docker run -d --name iot2batch -p 9092:9092 -p 8082:8082 -v C:\iot/h2:/db -v C:\iot/backup:/backup  iot2batch


docker exec -it iot2batch sh|bash
docker exec -it iot2batch endOfDay[.sh]
docker exec -it iot2batch iot2[.sh] -c end-of-day


Dockerfile: DockerfileIot2Ubuntu
docker system prune -f  --all --volumes
docker build -t iot2batch  -f docker/h2/DockerfileIot2Ubuntu .

docker run -d --name iot2batch  -p 9092:9092 -p 8082:8082 -v C:\iot/h2:/db -v C:\iot/backup:/backup  iot2batch 

docker save iot2batch  -o iot2batch
docker load -i iot2batch.tar 