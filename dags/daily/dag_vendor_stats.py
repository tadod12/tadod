from airflow import DAG
from airflow.utils.dates import days_ago
from airflow.operators.dummy_operator import DummyOperator
from airflow.providers.docker.operators.docker import DockerOperator
from docker.types import Mount
from datetime import datetime, timedelta

with DAG(
    dag_id='daily_vendor_stats_ver1',
    default_args={
        'owner': 'airflow',
        'depends_on_past': False,
        'email_on_failure': False,
        'email_on_retry': False,
        'retries': 1,
        'retry_delay': timedelta(minutes=5),
    },
    description='Yellow taxi DAG for vendor reports',
    schedule_interval="@daily",
    start_date=datetime(2020, 3, 1), # year, month, day
    end_date=datetime(2020, 3, 5), # year, month, day
    max_active_runs=1,
    catchup=True, # backfill from 2020
    tags=['daily', 'vendor'],
) as dag:
    
    start = DummyOperator(task_id="start_dag")

    yellow_cleaning = DockerOperator(
        task_id='yellow_cleaning',
        image='docker:latest',  # minimal image with Docker CLI
        command='docker exec spark-master sh /var/cleaning/template.sh YellowClean /var/cleaning/yellow.properties {{ ds }}',
        api_version='auto',
        auto_remove='success',
        docker_url='unix://var/run/docker.sock',
        network_mode='bridge',
        mounts=[
            Mount(source='/var/run/docker.sock', target='/var/run/docker.sock', type='bind'),
        ],
    )

    green_cleaning = DockerOperator(
        task_id='green_cleaning',
        image='docker:latest',  # minimal image with Docker CLI
        command='docker exec spark-master sh /var/cleaning/template.sh GreenClean /var/cleaning/green.properties {{ ds }}',
        api_version='auto',
        auto_remove='success',
        docker_url='unix://var/run/docker.sock',
        network_mode='bridge',
        mounts=[
            Mount(source='/var/run/docker.sock', target='/var/run/docker.sock', type='bind'),
        ],
    )

    yellow_vendor = DockerOperator(
        task_id='yellow_vendor',
        image='docker:latest',  # minimal image with Docker CLI
        command='docker exec spark-master sh /var/curation/template.sh YellowMartVendor /var/curation/yellow.properties {{ ds }}',
        api_version='auto',
        auto_remove='success',
        docker_url='unix://var/run/docker.sock',
        network_mode='bridge',
        mounts=[
            Mount(source='/var/run/docker.sock', target='/var/run/docker.sock', type='bind'),
        ],
    )

    green_vendor = DockerOperator(
        task_id='green_vendor',
        image='docker:latest',  # minimal image with Docker CLI
        command='docker exec spark-master sh /var/curation/template.sh GreenMartVendor /var/curation/green.properties {{ ds }}',
        api_version='auto',
        auto_remove='success',
        docker_url='unix://var/run/docker.sock',
        network_mode='bridge',
        mounts=[
            Mount(source='/var/run/docker.sock', target='/var/run/docker.sock', type='bind'),
        ],
    )
    

    end = DummyOperator(task_id="end_dag")

    start >> [yellow_cleaning, green_cleaning]
    yellow_cleaning >> yellow_vendor
    green_cleaning >> green_vendor
    [yellow_vendor, green_vendor] >> end
