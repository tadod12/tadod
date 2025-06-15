from datetime import datetime

from airflow import DAG
from airflow.operators.dummy_operator import DummyOperator
from airflow.providers.docker.operators.docker import DockerOperator
from docker.types import Mount

with DAG(
    dag_id="dag_monthly_transform_yellow",
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
    tags=["monthly", "transform"]
) as dag:
    start = DummyOperator(task_id="start_dag")

    yellow_cleaning = DockerOperator(
        task_id='yellow_cleaning',
        image='docker:latest',  # minimal image with Docker CLI
        command='docker exec spark-master sh /var/curation/template.sh YellowClean /var/cleaning/yellow.properties {{ ds }}',
        api_version='auto',
        auto_remove='success',
        docker_url='unix://var/run/docker.sock',
        network_mode='bridge',
        mounts=[
            Mount(source='/var/run/docker.sock', target='/var/run/docker.sock', type='bind'),
        ],
    )

    yellow_general = DockerOperator(
        task_id='yellow_general',
        image='docker:latest',  # minimal image with Docker CLI
        command='docker exec spark-master sh /var/curation/template.sh YellowGeneral /var/curation/yellow.properties {{ ds }}',
        api_version='auto',
        auto_remove='success',
        docker_url='unix://var/run/docker.sock',
        network_mode='bridge',
        mounts=[
            Mount(source='/var/run/docker.sock', target='/var/run/docker.sock', type='bind'),
        ],
    )

    yellow_location = DockerOperator(
        task_id='yellow_location',
        image='docker:latest',  # minimal image with Docker CLI
        command='docker exec spark-master sh /var/curation/template.sh YellowMartLocation /var/curation/yellow.properties {{ ds }}',
        api_version='auto',
        auto_remove='success',
        docker_url='unix://var/run/docker.sock',
        network_mode='bridge',
        mounts=[
            Mount(source='/var/run/docker.sock', target='/var/run/docker.sock', type='bind'),
        ],
    )

    yellow_payment = DockerOperator(
        task_id='yellow_payment',
        image='docker:latest',  # minimal image with Docker CLI
        command='docker exec spark-master sh /var/curation/template.sh YellowMartPayment /var/curation/yellow.properties {{ ds }}',
        api_version='auto',
        auto_remove='success',
        docker_url='unix://var/run/docker.sock',
        network_mode='bridge',
        mounts=[
            Mount(source='/var/run/docker.sock', target='/var/run/docker.sock', type='bind'),
        ],
    )

    yellow_ratecode = DockerOperator(
        task_id='yellow_ratecode',
        image='docker:latest',  # minimal image with Docker CLI
        command='docker exec spark-master sh /var/curation/template.sh YellowMartRateCode /var/curation/yellow.properties {{ ds }}',
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

    end = DummyOperator(task_id="end_dag")

    start >> yellow_cleaning >> yellow_general >> yellow_location >> yellow_payment >> yellow_ratecode >> yellow_vendor >> end
