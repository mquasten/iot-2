# iot2.batch mit Oracle XE Datenbank in einem Docker Container
# https://container-registry.oracle.com 

# download des Datenbankimages dauert ggf. 15 Minuten
docker system prune -f  --all --volumes

# docker build im Projektverzeichnis aufrufen 
docker build -t iot2batch  -f docker/oracle/DockerfileIot2 .

# container starten, wenn keine Datenbank existiert, wird eine neue erstellt.
docker run  -d --name iot2batch  -p 1522:1521  -e ORACLE_PWD=oracle -v C:\iot/oracle:/opt/oracle/oradata  -v C:\iot/backup:/backup  iot2batch 

docker logs -f iot2batch

docker exec -it iot2batch sqlplus system/oracle@XE
docker exec -it iot2batch bash
docker exec -it iot2batch endOfDay[.sh]
docker exec -it iot2batch iot2[.sh] -c end-of-day
docker exec -it iot2batch backup[.sh]


docker save -o iot2batchOracle.tar  iot2batch
docker load -i iot2batchOracle.tar 

Oracle Backup und Restore  der Datafiles 
tar -cvzf xe.tar.gz ./XE
tar -xvzf  xe.tar.gz ./XE

zurueckspielen eines Dumps fuer das schema iot2
docker exec -it iot2batch bash
impdp iot2/iot2@XE SCHEMAS=iot2 DIRECTORY=backup DUMPFILE=backup.dmp TABLE_EXISTS_ACTION=REPLACE