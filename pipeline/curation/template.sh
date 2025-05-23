#! /bin/bash

command="$1"
properties="$2"
date="$3"

echo "[INFO] Current date: $(date +"%Y-%m-%d %T")"

# Current time
# CURRENT_HOUR=$(date +"-H")
# CURRENT_DATE=$(date +"%Y-%m-%d")
# JARS_DIR=$SPARK_HOME/jars

spark_submit() {
    echo "[INFO] Processing with command: "$1""
    $SPARK_HOME/bin/spark-submit \
        --master local \
        --deploy-mode client \
        --driver-memory 4g \
        --executor-memory 4g \
        --executor-cores 2 \
        --num-executors 2 \
        --conf spark.sql.extensions=org.apache.iceberg.spark.extensions.IcebergSparkSessionExtensions \
        --conf spark.sql.catalog.spark_catalog=org.apache.iceberg.spark.SparkSessionCatalog \
        --conf spark.sql.catalog.spark_catalog.type=hive \
        --conf spark.sql.catalog.iceberg=org.apache.iceberg.spark.SparkCatalog \
        --conf spark.sql.catalog.iceberg.type=hive \
        --conf spark.sql.catalog.iceberg.uri=thrift://hive-metastore:9083 \
        --conf spark.sql.catalog.minio=org.apache.iceberg.spark.SparkCatalog \
        --conf spark.sql.catalog.minio.type=iceberg \
        --conf spark.sql.catalog.minio.uri=thrift://hive-metastore:9083 \
        --conf spark.hadoop.fs.s3a.access.key=minio \
        --conf spark.hadoop.fs.s3a.secret.key=minio123 \
        --conf spark.hadoop.fs.s3a.endpoint=http://minio:9000 \
        --conf spark.hadoop.fs.s3a.path.style.access=true \
        --conf spark.hadoop.fs.s3a.aws.credentials.provider=org.apache.hadoop.fs.s3a.SimpleAWSCredentialsProvider \
        --conf spark.sql.parquet.enableVectorizedReader=false \
        --conf spark.sql.catalog.minio.warehouse=s3a://datalake/ \
        --jars /var/jars/spark-sql-kafka-0-10_2.12-3.4.1.jar,/var/ingestion/jars/kafka-clients-3.4.1.jar,/var/ingestion/jars/spark-streaming-kafka-0-10_2.12-3.4.1.jar,/var/ingestion/jars/commons-pool2-2.11.1.jar,/var/ingestion/jars/spark-token-provider-kafka-0-10_2.12-3.4.1.jar,/var/ingestion/jars/aws-java-sdk-bundle-1.12.262.jar,/var/ingestion/jars/hadoop-aws-3.3.4.jar \
        --class com.tadod.App \
        /var/submit/jars/tadod-spark-1.0-jar-with-dependencies.jar \
        "$1" "$2" "$3" "$4"
        echo "[INFO] Spark app finished"
        sleep 1s
}

spark_submit "$command" "$properties" "$date" "$date"
