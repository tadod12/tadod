create schema iceberg.raw;

create table iceberg.raw.yellow (
	id BIGINT,
	vendor_id INT,
	tpep_pickup_datetime TIMESTAMP(6),
	tpep_dropoff_datetime TIMESTAMP(6),
	passenger_count INT,
	trip_distance DOUBLE,
	pu_location_id INT,
	do_location_id INT,
	rate_code_id INT,
	store_and_fwd_flag VARCHAR,
	payment_type INT,
	fare_amount DOUBLE,
	extra DOUBLE,
	mta_tax DOUBLE,
	improvement_surcharge DOUBLE,
	tip_amount DOUBLE,
	tolls_amount DOUBLE,
	total_amount DOUBLE,
	congestion_surcharge DOUBLE,
	airport_fee DOUBLE
)
WITH (
   format = 'PARQUET',
   format_version = 2,
   location = 's3://datalake/raw.db/yellow'
);