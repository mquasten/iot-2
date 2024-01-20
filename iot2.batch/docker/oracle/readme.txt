docker system prune -f  --all --volumes
docker build -t iot2batchoracle  -f docker/oracle/DockerfileIot2Oracle .
docker run -d  --name iot2batchoracle  -p 1522:1521  -e ORACLE_PWD=oracle -v C:\mq/oracle:/opt/oracle/oradata  -v C:\mq/h2/backup:/backup  iot2batchoracle 
docker exec -it iot2batchoracle sqlplus system/oracle@XE

docker run  -d --name iot2batchoracle  -p 1522:1521  -e ORACLE_PWD=oracle -v C:\mq/oracle:/opt/oracle/oradata  -v C:\mq/h2/backup:/backup  iot2batchoracle 


docker run  -d --name iot2batchoracle  -p 1522:1521  -e ORACLE_PWD=oracle -v C:\mq/oracle:/opt/oracle/oradata  -v C:\mq/h2/backup:/backup  iot2batchoracle 

docker save -o iot2batchoracle.tar  iot2batchoracle
docker load -i iot2.tar 

Oracle Backup und Restore  der Datafiles 
tar -cvzf xe.tar.gz ./XE
tar -xvzf  xe.tar.gz ./XE