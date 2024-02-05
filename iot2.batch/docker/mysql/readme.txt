# Container with MySql Database

docker system prune -f  --all --volumes

docker build -t iot2batch  -f docker/mysql/DockerfileIot2 .

docker run --name iot2batch  -p3306:3306   -v  C:\iot/mysql:/var/lib/mysql  -v C:\iot/backup:/backup    -d iot2batch

docker exec -it iot2batch bash

docker exec -it iot2batch sh|bash
docker exec -it iot2batch endOfDay[.sh]
docker exec -it iot2batch iot2[.sh] -c end-of-day