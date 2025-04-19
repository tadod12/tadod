create schema iceberg.clean;

create table iceberg.clean.yellow (
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
   partitioning = ARRAY['day(tpep_pickup_datetime)']
);

create table iceberg.clean.green (
	vendor_id INT,
	lpep_pickup_datetime TIMESTAMP(6) with TIME zone,
	lpep_dropoff_datetime TIMESTAMP(6) with TIME zone,
	store_and_fwd_flag VARCHAR,
	rate_code_id INT,
	pu_location_id INT,
	do_location_id INT,
	passenger_count INT,
	trip_distance DOUBLE,
	fare_amount DOUBLE,
	extra DOUBLE,
	mta_tax DOUBLE,
	tip_amount DOUBLE,
	tolls_amount DOUBLE,
	ehail_fee DOUBLE,
	improvement_surcharge DOUBLE,
	total_amount DOUBLE,
	payment_type INT,
	trip_type INT,
	congestion_surcharge DOUBLE
)
with (
	format = 'PARQUET',
	format_version = 2,
	partitioning = ARRAY['day(lpep_pickup_datetime)']
)