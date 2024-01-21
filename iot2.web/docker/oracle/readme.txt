# iot2.web und iot2.web  mit Oracle XE Datenbank in einem Docker Container
# container erbt von iot2batchoracle image, muß existieren und ggf. vorher gebaut werden

# download des Datenbankimages dauert ggf. 15 Minuten, danach muß man iot2batchoracle image neu bauen 
docker system prune -f  --all --volumes

# docker build im Projektverzeichnis aufrufen 
docker build -t iot2oracle  -f docker/oracle/DockerfileIot2Oracle .

docker run  -d --name iot2 -p 8080:8080 -p 1522:1521   -e ORACLE_PWD=oracle -v C:\iot/oracle:/opt/oracle/oradata  -v C:\iot/backup:/backup  iot2oracle 

docker logs -f iot2

docker exec -it iot2 bash


docker save -o iot2oracle.tar  iot2oracle
gzip iot2oracle.tar
docker load -i iot2oracle.tar.gz 
