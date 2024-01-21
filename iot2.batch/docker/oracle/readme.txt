# iot2.batch mit Oracle XE Datenbank in einem Docker Container
# https://container-registry.oracle.com 

# download des Datenbankimages dauert ggf. 15 Minuten
docker system prune -f  --all --volumes

# docker build im Projektverzeichnis aufrufen 
docker build -t iot2batchoracle  -f docker/oracle/DockerfileIot2Oracle .

# container starten, wenn keine Datenbank existiert, wird eine neue erstellt.
docker run  -d --name iot2batchoracle  -p 1522:1521  -e ORACLE_PWD=oracle -v C:\iot/oracle:/opt/oracle/oradata  -v C:\iot/backup:/backup  iot2batchoracle 

docker logs -f iot2batchoracle

docker exec -it iot2batchoracle sqlplus system/oracle@XE
docker exec -it iot2batchoracle bash
docker exec -it iot2batchoracle endOfDay[.sh]
docker exec -it iot2batchoracle iot2[.sh] -c end-of-day






docker save -o iot2batchoracle.tar  iot2batchoracle
docker load -i iot2.tar 

Oracle Backup und Restore  der Datafiles 
tar -cvzf xe.tar.gz ./XE
tar -xvzf  xe.tar.gz ./XE