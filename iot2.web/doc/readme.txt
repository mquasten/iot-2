docker system prune -f  --all --volumes
docker build -t iot2web .
docker run -d -p 8080:8080 -p 8082:8082 -p 9092:9092 -v C:/mq/h2:/db   iot2web