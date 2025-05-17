from airflow import DAG
from airflow.utils.dates import days_ago
from airflow.operators.dummy_operator import DummyOperator
from airflow.providers.docker.operators.docker import DockerOperator
from docker.types import Mount
from datetime import datetime, timedelta

with DAG(
    dag_id='dag_iceberg_compaction',
    default_args={
        'owner': 'airflow',
        'depends_on_past': False,
        'email_on_failure': False,
        'email_on_retry': False,
        'retries': 1,
        'retry_delay': timedelta(minutes=5),
    },
    start_date=days_ago(0),
    max_active_runs=1,
    description='Iceberg compaction DAG',
    schedule_interval="*/15 * * * *",  # every 15 minutes
    tags=['iceberg', 'compaction'],
) as dag:
    start = DummyOperator(task_id="start_dag")

    compaction = DockerOperator(
        task_id='iceberg_compaction',
        image='docker:latest',  # minimal image with Docker CLI
        command='docker exec spark-master sh /var/ingestion/submits/compact.sh \\{\\{ something \\}\\}',
        api_version='auto',
        auto_remove='success',
        docker_url='unix://var/run/docker.sock',
        network_mode='bridge',
        mounts=[
            Mount(source='/var/run/docker.sock', target='/var/run/docker.sock', type='bind'),
        ],
    )

    end = DummyOperator(task_id="end_dag")

    # Define task dependencies
    start >> compaction >> end
