-- Anlegen eines Benutzers sa fuer oracle
ALTER SESSION  SET "_ORACLE_SCRIPT"=true; 
-- DROP  USER  SA  CASCADE;
CREATE USER SA IDENTIFIED BY sa;
GRANT CONNECT TO sa;
GRANT RESOURCE TO sa;
ALTER USER sa QUOTA UNLIMITED ON users;