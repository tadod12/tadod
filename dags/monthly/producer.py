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

        producer = Producer({'bootstrap.servers': 'kafka1:9092,kafka2:9093,kafka3:9094'})
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
        # TODO: Change to green taxi data
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

        producer = Producer({'bootstrap.servers': 'kafka1:9092,kafka2:9093,kafka3:9094'})
        for index, row in df.iterrows():
            message = row.to_dict()
            print(f"Producing message: {message}")
            producer.produce('yellow', key=str(index), value=json.dumps(message), callback=delivery_report)
            producer.flush()
    else:
        print("No file found for yellow taxi data")
    print(f"Producing messages for {file_path}")


def produce_fhv(**kwargs):
    file_path = kwargs['ti'].xcom_pull(key='fhv')
    if file_path:
        print(f"Producing messages for {file_path}")
    else:
        print("No file found for yellow taxi data")
    print(f"Producing messages for {file_path}")


def produce_fhvhv(**kwargs):
    file_path = kwargs['ti'].xcom_pull(key='fhvhv')
    if file_path:
        print(f"Producing messages for {file_path}")
    else:
        print("No file found for yellow taxi data")
    print(f"Producing messages for {file_path}")


def delivery_report(err, msg):
    if err is not None:
        print('Message delivery failed: {}'.format(err))
    else:
        print('Message delivered to {} [{}]'.format(msg.topic(), msg.partition()))