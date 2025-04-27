from datetime import datetime

from airflow import DAG
from airflow.operators.dummy_operator import DummyOperator
from airflow.operators.python_operator import PythonOperator

from monthly.crawler import download_files
from monthly.producer import produce_yellow, produce_green, produce_fhv, produce_fhvhv

with DAG(
    dag_id="monthly_crawler_testing_ver9",
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
        provide_context=True,
    )

    download_green_taxi = PythonOperator(
        task_id="download_green_taxi",
        python_callable=download_files,
        op_kwargs={
            'type': 'green',
            'title': 'Green Taxi Trip Records',
            'date_run': '{{ ds }}'
        },
        provide_context=True,
    )

    download_fhv_taxi = PythonOperator(
        task_id="download_fhv_taxi",
        python_callable=download_files,
        op_kwargs={
            'type': 'fhv',
            'title': 'For-Hire Vehicle Trip Records',
            'date_run': '{{ ds }}'
        },
        provide_context=True,
    )

    # Remove High Volume For-Hire Vehicle Trip Records - Not enough RAM
    # download_fhvhv_taxi = PythonOperator(
    #     task_id="download_fhvhv_taxi",
    #     python_callable=download_files,
    #     op_kwargs={
    #         'type': 'fhvhv',
    #         'title': 'High Volume For-Hire Vehicle Trip Records',
    #         'date_run': '{{ ds }}'
    #     },
    #     provide_context=True,
    # )

    produce_yellow_taxi = PythonOperator(
        task_id="produce_yellow_taxi",
        python_callable=produce_yellow,
        provide_context=True,
    )

    produce_green_taxi = PythonOperator(
        task_id="produce_green_taxi",
        python_callable=produce_green,
        provide_context=True,
    )

    produce_fhv_taxi = PythonOperator(
        task_id="produce_fhv_taxi",
        python_callable=produce_fhv,
        provide_context=True,
    )

    # produce_fhvhv_taxi = PythonOperator(
    #     task_id="produce_fhvhv_taxi",
    #     python_callable=produce_fhvhv,
    #     provide_context=True,
    # )

    end = DummyOperator(task_id="end_dag")

    start >> [download_yellow_taxi, download_green_taxi, download_fhv_taxi]
    download_yellow_taxi >> produce_yellow_taxi
    download_green_taxi >> produce_green_taxi
    download_fhv_taxi >> produce_fhv_taxi
    [produce_yellow_taxi, produce_green_taxi, produce_fhv_taxi] >> end
