from airflow import DAG
from airflow.utils.dates import days_ago
from airflow.operators.dummy_operator import DummyOperator
from airflow.providers.docker.operators.docker import DockerOperator
from docker.types import Mount

with DAG(
    dag_id='yellow_taxi_vendor_stats',
    schedule_interval=@daily,
    default_args={
        'owner': 'airflow',
    },
    start_date=days_ago(1),
    end_date=None,
    max_active_runs=1,
    catchup=True,
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

    start >> spark_job >> end
