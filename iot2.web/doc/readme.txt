docker system prune -f  --all --volumes
docker build -t iot2web .
docker run -d -p 8080:8080 -v C:/mq/h2:/db iot2web