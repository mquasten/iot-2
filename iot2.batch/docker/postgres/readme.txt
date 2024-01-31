# Container with PostgreSQL Database

docker system prune -f  --all --volumes

docker build -t iot2batch  -f docker/postgres/DockerfileIot2 .

docker run -d  --name iot2batch -v C:\iot/postgres:/var/lib/postgresql/data -v C:\iot/backup:/backup -p 5432:5432  iot2batch

docker exec -it iot2batch bash

docker exec -it iot2batch sh|bash
docker exec -it iot2batch endOfDay[.sh]
docker exec -it iot2batch iot2[.sh] -c end-of-day

