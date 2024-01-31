#!/bin/bash
set -e

psql -v ON_ERROR_STOP=1 --username postgres --dbname "$POSTGRES_DB" <<-EOSQL
	CREATE USER iot2 WITH PASSWORD 'iot2';
	GRANT ALL PRIVILEGES ON DATABASE  iot TO iot2;
    GRANT ALL ON SCHEMA public TO iot2;
EOSQL

psql -v ON_ERROR_STOP=1 --username iot2 --dbname "$POSTGRES_DB" -f /docker-entrypoint-initdb.d/iot-2-postgres