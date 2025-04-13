from airflow import DAG
from airflow.utils.dates import days_ago
from airflow.providers.apache.spark.operators.spark_submit import SparkSubmitOperator
from airflow.operators.dummy_operator import DummyOperator

with DAG(
    dag_id="dag_spark_daily",
    start_date=days_ago(1),
    schedule_interval="@daily",
    catchup=False,
    default_args={
        "owner": "airflow",
        # "retries": 1,
        # "retry_delay": timedelta(minutes=5),
    }
) as dag:
    # Start task (DummyOperator for structure)
    start = DummyOperator(task_id="start_dag")

    # Cleaning job
    clean = SparkSubmitOperator(
        task_id="spark_cleaning_job",
        name="spark_cleaning_job",
        conn_id="spark-conn",
        application="/var/submit/jars/tadod-spark-1.0-jar-with-dependencies.jar",
        java_class="com.tadod.App",
        application_args=[
            str("YellowClean"),
            str("/var/cleaning/application.properties"),
            str("2024-02-02"),
            str("2024-02-02")
        ],
        driver_memory="8g",
        executor_memory="8g",
        executor_cores=2,
        num_executors=2,
        conf={
            "spark.sql.extensions": "org.apache.iceberg.spark.extensions.IcebergSparkSessionExtensions",
            "spark.sql.catalog.spark_catalog": "org.apache.iceberg.spark.SparkSessionCatalog",
            "spark.sql.catalog.spark_catalog.type": "hive",
            "spark.sql.catalog.iceberg": "org.apache.iceberg.spark.SparkCatalog",
            "spark.sql.catalog.iceberg.type": "hive",
            "spark.sql.catalog.iceberg.uri": "thrift://hive-metastore:9083",
            "spark.sql.catalog.minio": "org.apache.iceberg.spark.SparkCatalog",
            "spark.sql.catalog.minio.type": "iceberg",
            "spark.sql.catalog.minio.uri": "thrift://hive-metastore:9083",
            "spark.hadoop.fs.s3a.impl": "org.apache.hadoop.fs.s3a.S3AFileSystem",
            "spark.hadoop.fs.s3.impl": "org.apache.hadoop.fs.s3a.S3AFileSystem",
            "spark.hadoop.fs.s3a.access.key": "minio",
            "spark.hadoop.fs.s3a.secret.key": "minio123",
            "spark.hadoop.fs.s3a.endpoint": "http://minio:9000",
            "spark.hadoop.fs.s3a.path.style.access": "true",
            "spark.hadoop.fs.s3a.aws.credentials.provider": "org.apache.hadoop.fs.s3a.SimpleAWSCredentialsProvider",
            "spark.sql.parquet.enableVectorizedReader": "false",
            "spark.sql.catalog.minio.warehouse": "s3a://datalake/"
        },
        jars="/var/jars/spark-sql-kafka-0-10_2.12-3.4.1.jar,/var/jars/kafka-clients-3.4.1.jar,/var/jars/spark-streaming-kafka-0-10_2.12-3.4.1.jar,/var/jars/commons-pool2-2.11.1.jar,/var/jars/spark-token-provider-kafka-0-10_2.12-3.4.1.jar,/var/jars/aws-java-sdk-bundle-1.12.262.jar,/var/jars/hadoop-aws-3.3.4.jar,/var/jars/iceberg-spark-runtime-3.4_2.12-1.4.3.jar",
        verbose=True
    )

    # End dag
    end = DummyOperator(task_id="end_dag")

    # Dependency graph
    start >> clean >> end
