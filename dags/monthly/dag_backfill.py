from datetime import datetime

from airflow import DAG
from airflow.operators.dummy_operator import DummyOperator
from airflow.operators.python_operator import PythonOperator
from airflow.providers.docker.operators.docker import DockerOperator
from docker.types import Mount

from monthly.crawler import download_files

with DAG(
    dag_id="dag_monthly_crawler_yellow_fill",
    default_args={
        'owner': 'airflow',
        'depends_on_past': False,
        'email_on_failure': False,
        'email_on_retry': False,
    },
    description="Monthly crawling for latest data",
    schedule_interval="@monthly",
    start_date=datetime(2020, 1, 1),
    end_date=datetime(2025, 1, 1),
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
        provide_context=True,
    )

    yellow_dumping = DockerOperator(
        task_id='yellow_cleaning',
        image='docker:latest',  # minimal image with Docker CLI
        command='docker exec spark-master sh /var/curation/template.sh YellowDump /var/curation/yellow.properties {{ ds }}',
        api_version='auto',
        auto_remove='success',
        docker_url='unix://var/run/docker.sock',
        network_mode='bridge',
        mounts=[
            Mount(source='/var/run/docker.sock', target='/var/run/docker.sock', type='bind'),
        ],
    )

    end = DummyOperator(task_id="end_dag")

    start >> download_yellow_taxi >> yellow_dumping >> end
