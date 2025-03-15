import os
import pandas as pd
import mysql.connector
from mysql.connector import Error
from sqlalchemy import create_engine

# Data Source
data_dir = "./data/"

# Destination Configuration
mysql_config = {
    'host': 'mysql',
    'port': '3306',
    'user': 'root',
    'password': 'admin',
    'database': 'tlc'
}
table_name = "yellow"


def produce():
    engine = create_engine(f"mysql+mysqlconnector://root:admin@mysql:3306/tlc")

    for file in os.listdir(data_dir):
        print(f"Inserting data from {file}")

        df = pd.read_parquet(os.path.join(data_dir, file))
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

        try:
            df.to_sql(
                name=table_name,
                con=engine,
                if_exists='append',
                index=False,
                chunksize=1000
            )
            print(f"Successfully inserted data from {file}")
        except Exception as e:
            print(f"Failed to insert data from {file}")
            print(e)

if __name__ == "__main__":
    produce()
