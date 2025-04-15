import os
import re
import requests
import urllib.request

from datetime import datetime
from bs4 import BeautifulSoup

from airflow import DAG
from airflow.operators.dummy_operator import DummyOperator
from airflow.operators.python_operator import PythonOperator


def download_files(type: str, title: str, date_run: str):
    """
    Download files from the NYC TLC website based on the type of taxi data and the execution date.
    Args:
        type (str): Type of taxi data to download (e.g., 'yellow', 'green', 'fhv').
        date_run (str): Execution date in the format 'YYYY-MM-DD'.
        Returns: True if files were downloaded successfully, False otherwise.
    """
    print(f"Running download_files with type: {type} and date_run: {date_run}")
    page_url = page_url = 'https://www.nyc.gov/site/tlc/about/tlc-trip-record-data.page'
    download_dir = f'/var/data/{type}'
    os.makedirs(download_dir, exist_ok=True)
    response = requests.get(page_url)

    exec_date = datetime.strptime(date_run, '%Y-%m-%d')
    exec_month, exec_year = exec_date.month, exec_date.year

    if response.status_code == 200:
        soup = BeautifulSoup(response.text, 'html.parser')
        links = soup.find_all('a', title=title)
        print(f"Found {len(links)} links for {type} taxi data")
        for link in links:
            link_url = link.get('href')
            # print(f"Processing link: {link_url}")
            # print(f"{type}_tripdata_{exec_year}-{exec_month:02d}")
            if f"{type}_tripdata_{exec_year}-{exec_month:02d}.parquet" in link_url:
                file_name = os.path.join(download_dir, link_url.split('/')[-1])
                print(f"Downloading {link_url.split('/')[-1]}...")
                urllib.request.urlretrieve(url=link_url, filename=file_name)
                print(f"Downloaded {link_url.split('/')[-1]}")
            else:
                print(f"Skipping {link_url.split('/')[-1]}: does not match expected pattern")
    else:
        print(f"Error: {response.status_code}")


with DAG(
    dag_id="monthly_crawler_testing_ver5",
    default_args={
        'owner': 'airflow',
        'depends_on_past': False,
        'email_on_failure': False,
        'email_on_retry': False,
    },
    description="Monthly crawling for latest data",
    schedule_interval="@monthly",
    start_date=datetime(2020, 1, 1),
    end_date=datetime(2021, 1, 1),
    max_active_runs=1,
    catchup=True,
    tags=["monthly", "crawler"]
) as dag:
    start = DummyOperator(task_id="start_dag")

    download_yellow_taxi = PythonOperator(
        task_id="download_yellow_taxi",
        python_callable=download_files,
        op_kwargs={
            'type': 'yellow',
            'title': 'Yellow Taxi Trip Records',
            'date_run': '{{ ds }}'
        },
    )

    download_green_taxi = PythonOperator(
        task_id="download_green_taxi",
        python_callable=download_files,
        op_kwargs={
            'type': 'green',
            'title': 'Green Taxi Trip Records',
            'date_run': '{{ ds }}'
        },
    )

    download_fhv_taxi = PythonOperator(
        task_id="download_fhv_taxi",
        python_callable=download_files,
        op_kwargs={
            'type': 'fhv',
            'title': 'For-Hire Vehicle Trip Records',
            'date_run': '{{ ds }}'
        },
    )

    end = DummyOperator(task_id="end_dag")

    start >> [download_yellow_taxi, download_green_taxi, download_fhv_taxi] >> end
