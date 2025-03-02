-- Grant Permissions
GRANT REPLICATION SLAVE, REPLICATION CLIENT ON *.* TO 'root' IDENTIFIED BY 'admin';
GRANT SELECT, RELOAD, SHOW DATABASES, REPLICATION SLAVE, REPLICATION CLIENT ON *.* TO 'root' IDENTIFIED BY 'admin';

CREATE DATABASE tlc_record;
GRANT ALL PRIVILEGES ON tlc_record.* TO 'root'@'%';

-- Switch to this database
USE tlc_record;

-- Create location lookup table
-- CREATE TABLE location (
--     location_id INT PRIMARY KEY,
--     borough VARCHAR(30),
--     zone VARCHAR(50),
--     service_zone VARCHAR(30)
-- );

-- Create table for yellow taxi record data
CREATE TABLE yellow_records (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
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
    airport_fee FLOAT,
    FOREIGN KEY (pu_location_id) REFERENCES location(location_id),
    FOREIGN KEY (do_location_id) REFERENCES location(location_id)
);

-- Create table for green taxi record data
CREATE TABLE green_records (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    vendor_id INT,
    lpep_pickup_datetime VARCHAR(30),
    lpep_dropoff_datetime VARCHAR(30),
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
    trip_type INT,
    FOREIGN KEY (pu_location_id) REFERENCES location(location_id),
    FOREIGN KEY (do_location_id) REFERENCES location(location_id)
);

-- Create table for for-hire vehicle record data
CREATE TABLE fhv_records (
    dispatching_base_num VARCHAR(10),
    pickup_datetime VARCHAR(30),
    dropoff_datetime VARCHAR(30),
    pu_location_id INT,
    do_location_id INT,
    sr_flag VARCHAR(1),
    FOREIGN KEY (pu_location_id) REFERENCES location(location_id),
    FOREIGN KEY (do_location_id) REFERENCES location(location_id)
);
