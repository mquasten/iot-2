-- create user oracle
-- sqlplus system/oracle@//localhost:1521/XE
-- sqlplus sys/oracle@//localhost:1521/XE as sysdba

whenever sqlerror exit;

alter session set "_ORACLE_SCRIPT"=true;  

create user iot2 identified by iot2;
grant unlimited tablespace to iot2;

grant connect to iot2;
GRANT RESOURCE TO iot2;
