import os
import pandas as pd
import mysql.connector
from mysql.connector import Error

data_dir = "./data/"


def data():
    for file in os.listdir(data_dir):
        print(file)
        df = pd.read_parquet(os.path.join(data_dir, file))
        print(df.head())


def connect():
    """Connect to MySQL database"""
    conn = None
    try:
        conn = mysql.connector.connect(
            user="root",
            password="admin",
            host="mysql",
            port="3306",
            database="tlc"
        )
        if conn.is_connected():
            print("Connected to MySQL database")
    except Error as e:
        print(e)
    finally:
        if conn is not None and conn.is_connected():
            conn.close()
            print("MySQL connection is closed")


if __name__ == "__main__":
    data()
    connect()
