import os
import requests
import urllib.request
import json

from datetime import datetime
from bs4 import BeautifulSoup


def download_files(type: str, title: str, date_run: str, **kwargs):
    """
    Download files from the NYC TLC website based on the type of taxi data and the execution date (month & year)
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
                kwargs['ti'].xcom_push(key=f'{type}', value=f'/var/data/{type}/{link_url.split("/")[-1]}')
                break
            else:
                kwargs['ti'].xcom_push(key=f'{type}', value=None)
                print(f"Skipping {link_url.split('/')[-1]}: does not match expected pattern")
    else:
        print(f"Error: {response.status_code}")
