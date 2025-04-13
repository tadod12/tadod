from airflow import DAG
from airflow.utils.dates import days_ago
from airflow.operators.python import PythonOperator
from airflow.operators.bash import BashOperator

dag = DAG(
    dag_id="dag_test",
    default_args={
        "owner": "tadod",
        "start_date": days_ago(1),
    },
    schedule_interval="@hourly",
    tags=["example"]
)

python_check = PythonOperator(
    task_id="python_check",
    python_callable=lambda: print("Python check"),
    dag=dag,
)

bash_check = BashOperator(
    task_id="bash_check",
    bash_command="echo 'Bash check'",
    dag=dag,
)

python_check >> bash_check