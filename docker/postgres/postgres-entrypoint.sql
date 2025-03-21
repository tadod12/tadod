-- Grant Permissions
-- In PostgreSQL, replication privileges are handled differently. We'll create a replication role if needed.
-- Note: 'REPLICATION SLAVE' and 'REPLICATION CLIENT' are MySQL-specific; PostgreSQL uses 'REPLICATION'.
DO
$$
BEGIN
   IF NOT EXISTS (SELECT FROM pg_roles WHERE rolname = 'postgres') THEN
      CREATE ROLE postgres WITH LOGIN PASSWORD 'postgres';
   END IF;
   ALTER ROLE postgres WITH REPLICATION; -- Grants replication privileges
END;
$$;

GRANT SELECT ON ALL TABLES IN SCHEMA public TO postgres;

-- Create the 'tlc' database if it doesn't exist
DO
$$
BEGIN
   IF NOT EXISTS (SELECT FROM pg_database WHERE datname = 'tlc') THEN
      CREATE DATABASE tlc;
   END IF;
END;
$$;

GRANT ALL PRIVILEGES ON DATABASE tlc TO postgres;

-- Drop tables if they exist
DROP TABLE IF EXISTS yellow CASCADE;
DROP TABLE IF EXISTS green CASCADE;
DROP TABLE IF EXISTS fhv CASCADE;

-- Create table for yellow taxi record data
CREATE TABLE IF NOT EXISTS yellow (
    id BIGSERIAL PRIMARY KEY, -- AUTO_INCREMENT becomes BIGSERIAL in PostgreSQL
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

-- Create table for green taxi record data (commented out in original, uncomment if needed)
-- CREATE TABLE IF NOT EXISTS green (
--     id BIGSERIAL PRIMARY KEY,
--     vendor_id INT,
--     lpep_pickup_datetime VARCHAR(30),
--     lpep_dropoff_datetime VARCHAR(30),
--     passenger_count INT,
--     trip_distance FLOAT,
--     pu_location_id INT,
--     do_location_id INT,
--     rate_code_id INT,
--     store_and_fwd_flag VARCHAR(1),
--     payment_type INT,
--     fare_amount FLOAT,
--     extra FLOAT,
--     mta_tax FLOAT,
--     improvement_surcharge FLOAT,
--     tip_amount FLOAT,
--     tolls_amount FLOAT,
--     trip_type INT
-- );

-- Create table for for-hire vehicle record data (commented out in original, uncomment if needed)
-- CREATE TABLE IF NOT EXISTS fhv (
--     id BIGSERIAL PRIMARY KEY,
--     dispatching_base_num VARCHAR(10),
--     pickup_datetime VARCHAR(30),
--     dropoff_datetime VARCHAR(30),
--     pu_location_id INT,
--     do_location_id INT,
--     sr_flag VARCHAR(1)
-- );