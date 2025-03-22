#!/bin/bash
set -e

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
    CREATE DATABASE metastore;
    CREATE USER hive WITH ENCRYPTED PASSWORD 'hive';
    GRANT ALL PRIVILEGES ON DATABASE metastore TO hive;

    CREATE DATABASE airflow;
    CREATE USER airflow WITH ENCRYPTED PASSWORD 'airflow';
    GRANT ALL PRIVILEGES ON DATABASE airflow TO airflow;
    GRANT ALL PRIVILEGES ON DATABASE metastore TO airflow;

    CREATE DATABASE source;
    CREATE USER source WITH ENCRYPTED PASSWORD 'source';
    GRANT ALL PRIVILEGES ON DATABASE source TO source;
EOSQL

psql -v ON_ERROR_STOP=1 --username "source" --dbname "source" <<-EOSQL
    CREATE SCHEMA source_schema AUTHORIZATION source;
    CREATE TABLE source_schema.yellow (
        id BIGSERIAL PRIMARY KEY,
        vendor_id INT,
        tpep_pickup_datetime VARCHAR(30),
        tpep_dropoff_datetime VARCHAR(30),
        passenger_count INT,
        trip_distance FLOAT,
        pu_location_id INT,
        do_location_id INT,
        rate_code_id INT,
        store_and_fwd_flag VARCHAR(1),
        payment_type INT,
        fare_amount FLOAT,
        extra FLOAT,
        mta_tax FLOAT,
        improvement_surcharge FLOAT,
        tip_amount FLOAT,
        tolls_amount FLOAT,
        total_amount FLOAT,
        congestion_surcharge FLOAT,
        airport_fee FLOAT
    );
EOSQL
