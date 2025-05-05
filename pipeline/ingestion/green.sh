#! /bin/bash

echo "[INFO] Current date: $(date +"%Y-%m-%d %T")"

# Current time
CURRENT_HOUR=$(date +"-H")
CURRENT_DATE=$(date +"%Y-%m-%d")

spark_submit() {
    $SPARK_HOME/bin/spark-submit \
        --master local \
        --deploy-mode client \
        --driver-memory 1g \
        --executor-memory 1g \
        --executor-cores 1 \
        --num-executors 2 \
        --conf spark.sql.extensions=org.apache.iceberg.spark.extensions.IcebergSparkSessionExtensions \
        --conf spark.sql.catalog.iceberg=org.apache.iceberg.spark.SparkCatalog \
        --conf spark.sql.catalog.iceberg.type=hive \
        --conf spark.sql.catalog.iceberg.uri=thrift://hive-metastore:9083 \
        --conf spark.hadoop.fs.s3a.access.key=minio \
        --conf spark.hadoop.fs.s3a.secret.key=minio123 \
        --conf spark.hadoop.fs.s3a.endpoint=http://minio:9000 \
        --conf spark.hadoop.fs.s3a.path.style.access=true \
        --conf spark.hadoop.fs.s3a.aws.credentials.provider=org.apache.hadoop.fs.s3a.SimpleAWSCredentialsProvider \
        --conf spark.sql.parquet.enableVectorizedReader=false \
        --conf spark.sql.catalog.minio.warehouse=s3a://datalake/ \
        --class com.tadod.App \
        /var/submit/jars/tadod-spark-1.0-jar-with-dependencies.jar \
        "$1" "$2" "$3" "$4"
        echo "[INFO] Spark app finished"
        sleep 1s
}

spark_submit "GreenStream" "/var/ingestion/green.properties" $CURRENT_DATE $CURRENT_DATE
