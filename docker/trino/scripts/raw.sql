create schema iceberg.raw;

create table iceberg.raw.yellow (
	vendor_id INT,
	tpep_pickup_datetime TIMESTAMP(6) WITH TIME ZONE,
	tpep_dropoff_datetime TIMESTAMP(6) WITH TIME ZONE,
	passenger_count INT,
	trip_distance DOUBLE,
	rate_code_id INT,
	store_and_fwd_flag VARCHAR,
	pu_location_id INT,
	do_location_id INT,
	payment_type INT,
	fare_amount DOUBLE,
	extra DOUBLE,
	mta_tax DOUBLE,
	tip_amount DOUBLE,
	tolls_amount DOUBLE,
	improvement_surcharge DOUBLE,
	total_amount DOUBLE,
	congestion_surcharge DOUBLE,
	airport_fee DOUBLE
)
WITH (
   format = 'PARQUET',
   format_version = 2,
   location = 's3://datalake/raw.db/yellow',
   partitioning = ARRAY['day(tpep_pickup_datetime)']
);

--drop table iceberg.raw.yellow;


create table iceberg.raw.green (
	vendor_id INT,
	lpep_pickup_datetime TIMESTAMP(6) WITH TIME ZONE,
	lpep_dropoff_datetime TIMESTAMP(6) WITH TIME ZONE,
	passenger_count INT,
	trip_distance DOUBLE,
	rate_code_id INT,
	store_and_fwd_flag VARCHAR,
	pu_location_id INT,
	do_location_id INT,
	payment_type INT,
	fare_amount DOUBLE,
	extra DOUBLE,
	mta_tax DOUBLE,
	tip_amount DOUBLE,
	tolls_amount DOUBLE,
	improvement_surcharge DOUBLE,
	total_amount DOUBLE,
	congestion_surcharge DOUBLE,
	trip_type INT,
	ehail_fee DOUBLE
)
WITH (
   format = 'PARQUET',
   format_version = 2,
   partitioning = ARRAY['day(lpep_pickup_datetime)']
);
