import pandas as pd
from confluent_kafka import Producer
import json


def produce_yellow(**kwargs):
    file_path = kwargs['ti'].xcom_pull(key='yellow')
    if file_path:
        print(f"=== Producing messages for {file_path} ===")
        df = pd.read_parquet(file_path, engine='pyarrow')
        df = df.rename(columns={
            'VendorID': 'vendor_id',
            'tpep_pickup_datetime': 'tpep_pickup_datetime',
            'tpep_dropoff_datetime': 'tpep_dropoff_datetime',
            'passenger_count': 'passenger_count',
            'trip_distance': 'trip_distance',
            'PULocationID': 'pu_location_id',
            'DOLocationID': 'do_location_id',
            'RatecodeID': 'rate_code_id',
            'store_and_fwd_flag': 'store_and_fwd_flag',
            'payment_type': 'payment_type',
            'fare_amount': 'fare_amount',
            'extra': 'extra',
            'mta_tax': 'mta_tax',
            'improvement_surcharge': 'improvement_surcharge',
            'tip_amount': 'tip_amount',
            'tolls_amount': 'tolls_amount',
            'total_amount': 'total_amount',
            'congestion_surcharge': 'congestion_surcharge',
            'Airport_fee': 'airport_fee'
        })

        df['tpep_pickup_datetime'] = df['tpep_pickup_datetime'].apply(
            lambda x: x.strftime('%Y-%m-%d %H:%M:%S') if pd.notna(x) else None
        )

        df['tpep_dropoff_datetime'] = df['tpep_dropoff_datetime'].apply(
            lambda x: x.strftime('%Y-%m-%d %H:%M:%S') if pd.notna(x) else None
        )

        producer = Producer({'bootstrap.servers': 'kafka-1:29092,kafka-2:29093,kafka-3:29094'})
        for index, row in df.iterrows():
            message = row.to_dict()
            print(f"Producing message: {message}")
            producer.produce('yellow', key=str(index), value=json.dumps(message), callback=delivery_report)
            producer.flush()
    else:
        print("No file found for yellow taxi data")


def produce_green(**kwargs):
    file_path = kwargs['ti'].xcom_pull(key='green')
    if file_path:
        print(f"=== Producing messages for {file_path} ===")
        df = pd.read_parquet(file_path, engine='pyarrow')
        df = df.rename(columns={
            'VendorID': 'vendor_id',
            'lpep_pickup_datetime': 'lpep_pickup_datetime',
            'lpep_dropoff_datetime': 'lpep_dropoff_datetime',
            'passenger_count': 'passenger_count',
            'trip_distance': 'trip_distance',
            'RatecodeID': 'rate_code_id',
            'store_and_fwd_flag': 'store_and_fwd_flag',
            'PULocationID': 'pu_location_id',
            'DOLocationID': 'do_location_id',
            'payment_type': 'payment_type',
            'fare_amount': 'fare_amount',
            'extra': 'extra',
            'mta_tax': 'mta_tax',
            'tip_amount': 'tip_amount',
            'tolls_amount': 'tolls_amount',
            'improvement_surcharge': 'improvement_surcharge',
            'total_amount': 'total_amount',
            'congestion_surcharge': 'congestion_surcharge',
            'trip_type': 'trip_type',
            'cbd_congestion_fee': 'cbd_congestion_fee'
        })

        df['lpep_pickup_datetime'] = df['lpep_pickup_datetime'].apply(
            lambda x: x.strftime('%Y-%m-%d %H:%M:%S') if pd.notna(x) else None
        )

        df['lpep_dropoff_datetime'] = df['lpep_dropoff_datetime'].apply(
            lambda x: x.strftime('%Y-%m-%d %H:%M:%S') if pd.notna(x) else None
        )

        producer = Producer({'bootstrap.servers': 'kafka-1:29092,kafka-2:29093,kafka-3:29094'})
        for index, row in df.iterrows():
            message = row.to_dict()
            print(f"Producing message: {message}")
            producer.produce('green', key=str(index), value=json.dumps(message), callback=delivery_report)
            producer.flush()
    else:
        print("No file found for green taxi data")
    print(f"Producing messages for {file_path}")


def produce_fhv(**kwargs):
    file_path = kwargs['ti'].xcom_pull(key='fhv')
    if file_path:
        print(f"=== Producing messages for {file_path} ===")
        df = pd.read_parquet(file_path, engine='pyarrow')
        df = df.rename(columns={
            'dispatching_base_num': 'dispatching_base_num',
            'pickup_datetime': 'pickup_datetime',
            'dropOff_datetime': 'dropoff_datetime',
            'PUlocationID': 'pu_location_id',
            'DOlocationID': 'do_location_id',
            'SR_Flag': 'sr_flag',
            'Affiliated_base_number': 'affiliated_base_number'
        })

        df['pickup_datetime'] = df['pickup_datetime'].apply(
            lambda x: x.strftime('%Y-%m-%d %H:%M:%S') if pd.notna(x) else None
        )

        df['dropoff_datetime'] = df['dropoff_datetime'].apply(
            lambda x: x.strftime('%Y-%m-%d %H:%M:%S') if pd.notna(x) else None
        )

        producer = Producer({'bootstrap.servers': 'kafka-1:29092,kafka-2:29093,kafka-3:29094'})
        for index, row in df.iterrows():
            message = row.to_dict()
            print(f"Producing message: {message}")
            producer.produce('fhv', key=str(index), value=json.dumps(message), callback=delivery_report)
            producer.flush()
    else:
        print("No file found for fhv taxi data")
    print(f"Producing messages for {file_path}")


def produce_fhvhv(**kwargs):
    file_path = kwargs['ti'].xcom_pull(key='fhvhv')
    if file_path:
        print(f"=== Producing messages for {file_path} ===")
        df = pd.read_parquet(file_path, engine='pyarrow')
        df = df.rename(columns={
            'hvfhs_license_num': 'hvfhs_license_num',
            'dispatching_base_num': 'dispatching_base_num',
            'originating_base_num': 'originating_base_num',
            'request_datetime': 'request_datetime',             #
            'on_scene_datetime': 'on_scene_datetime',           #
            'pickup_datetime': 'pickup_datetime',               #
            'dropoff_datetime': 'dropoff_datetime',             #
            'PUlocationID': 'pu_location_id',
            'DOlocationID': 'do_location_id',
            'trip_miles': 'trip_miles',
            'trip_time': 'trip_time',
            'base_passenger_fare': 'base_passenger_fare',
            'tolls': 'tolls',
            'bcf': 'bcf',
            'sales_tax': 'sales_tax',
            'congestion_surcharge': 'congestion_surcharge',
            'airport_fee': 'airport_fee',
            'tips': 'tips',
            'driver_pay': 'driver_pay',
            'shared_request_flag': 'shared_request_flag',
            'shared_match_flag': 'shared_match_flag',
            'access_a_ride_flag': 'access_a_ride_flag',
            'wav_request_flag': 'wav_request_flag',
            'wav_match_flag': 'wav_match_flag',
            # 'cbd_congestion_fee': 'cbd_congestion_fee',
        })

        df['request_datetime'] = df['request_datetime'].apply(
            lambda x: x.strftime('%Y-%m-%d %H:%M:%S') if pd.notna(x) else None
        )

        df['on_scene_datetime'] = df['on_scene_datetime'].apply(
            lambda x: x.strftime('%Y-%m-%d %H:%M:%S') if pd.notna(x) else None
        )

        df['pickup_datetime'] = df['pickup_datetime'].apply(
            lambda x: x.strftime('%Y-%m-%d %H:%M:%S') if pd.notna(x) else None
        )

        df['dropoff_datetime'] = df['dropoff_datetime'].apply(
            lambda x: x.strftime('%Y-%m-%d %H:%M:%S') if pd.notna(x) else None
        )

        producer = Producer({'bootstrap.servers': 'kafka-1:29092,kafka-2:29093,kafka-3:29094'})
        for index, row in df.iterrows():
            message = row.to_dict()
            print(f"Producing message: {message}")
            producer.produce('fhv', key=str(index), value=json.dumps(message), callback=delivery_report)
            producer.flush()
    else:
        print("No file found for hvfhv taxi data")
    print(f"Producing messages for {file_path}")


def delivery_report(err, msg):
    if err is not None:
        print('Message delivery failed: {}'.format(err))
    else:
        print('Message delivered to {} [{}]'.format(msg.topic(), msg.partition()))