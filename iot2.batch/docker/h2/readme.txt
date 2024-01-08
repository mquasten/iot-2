# startet eine Datenbank zu Testzwecken mit extermem Verzeichnis 
docker system prune -f  --all --volumes
docker build -t h2 .
docker run -d -p 9092:9092 -p 8082:8082 -v C:/mq/h2:/db  h2 

localhost/8082
Generic H2 (Server)
jdbc:h2:tcp://localhost/iot