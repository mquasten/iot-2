Dockerfile
# startet eine Datenbank zu Testzwecken mit extermem Verzeichnis 

docker system prune -f  --all --volumes
docker build -t h2 .
docker run -d -p 9092:9092 -p 8082:8082 -v C:/mq/h2:/db  h2 

localhost/8082
Generic H2 (Server)
jdbc:h2:tcp://localhost/iot



Dockerfile: Iot2H2
# docker image für iot2batch Alpine + Jave Runtime mit  musl libc  (without  glibc and friends)
docker system prune -f  --all --volumes
docker build -t iot2batch  -f docker/h2/DockerfileIot2H2 .
docker run -d --name iot2batch -p 9092:9092 -p 8082:8082 -v C:\iot/h2:/db -v C:\iot/backup:/backup  iot2batch


docker exec -it iot2batch sh
docker exec -it iot2batch endOfDay[.sh]
docker exec -it iot2batch iot2[.sh] -c end-of-day


Dockerfile: Iot2H2JRE
docker system prune -f  --all --volumes
docker build -t iot2batch  -f docker/h2/DockerfileIot2H2JRE .

docker run -d --name iot2batch iot2batch 

docker save -o iot2batch.tar  
docker load -i iot2batch.tar 