-- directory fuer export 
alter session set "_ORACLE_SCRIPT"=true; 
CREATE DIRECTORY backup AS '/backup';
grant read, write on directory backup to iot2;