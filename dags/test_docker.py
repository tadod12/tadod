from airflow import DAG
from airflow.utils.dates import days_ago
from airflow.operators.bash_operator import BashOperator
from airflow.operators.dummy_operator import DummyOperator
from airflow.providers.docker.operators.docker import DockerOperator
from docker.types import Mount

with DAG(
    dag_id='test_docker',
    schedule_interval=None,
    default_args={
        'owner': 'airflow',
    },
    catchup=False,
    tags=['test'],
) as dag:
    start = DummyOperator(task_id="start_dag")

    spark_job = DockerOperator(
        task_id='run_inside_existing',
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

    end = DummyOperator(task_id="end_dag")

    start >> spark_job >> end