-- create user oracle
-- sqlplus system/oracle@//localhost:1521/XEPDB1
-- sqlplus sys/oracle@//localhost:1521/XEPDB1 as sysdba

alter session set "_ORACLE_SCRIPT"=true;  

create user iot2 identified by iot2;
grant unlimited tablespace to iot2;

grant connect to iot2;
GRANT RESOURCE TO iot2;
