from airflow import DAG
from airflow.utils.dates import days_ago
from airflow.operators.dummy_operator import DummyOperator
from airflow.providers.docker.operators.docker import DockerOperator
from docker.types import Mount
from datetime import datetime, timedelta

with DAG(
    dag_id='yellow_taxi_vendor_stats',
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
    start_date=datetime(2020, 1, 1), # year, month, day
    max_active_runs=1,
    catchup=True, # backfill from 2020
    tags=['yellow_taxi', 'daily', 'vendor'],
) as dag:
    start = DummyOperator(task_id="start_dag")

    cleaning = DockerOperator(
        task_id='data_cleaning',
        image='docker:latest',  # minimal image with Docker CLI
        command='docker exec spark-master sh /var/cleaning/clean.sh ',
        api_version='auto',
        auto_remove='success',
        docker_url='unix://var/run/docker.sock',
        network_mode='bridge',
        mounts=[
            Mount(source='/var/run/docker.sock', target='/var/run/docker.sock', type='bind'),
        ],
    )

    loading = DockerOperator(
        task_id='data_loading',
        image='docker:latest',  # minimal image with Docker CLI
        command='docker exec spark-master sh /var/curation/template.sh MartVendor /var/curation/application.properties 2024-01-02',
        api_version='auto',
        auto_remove='success',
        docker_url='unix://var/run/docker.sock',
        network_mode='bridge',
        mounts=[
            Mount(source='/var/run/docker.sock', target='/var/run/docker.sock', type='bind'),
        ],
    )

    end = DummyOperator(task_id="end_dag")

    start >> cleaning >> loading >> end
