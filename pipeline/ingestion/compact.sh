#! /bin/bash

echo "[INFO] Current date: $(date +"%Y-%m-%d %T")"

# Current time
CURRENT_HOUR=$(date +"-H")
CURRENT_DATE=$(date +"%Y-%m-%d")

spark_submit() {
    $SPARK_HOME/bin/spark-submit \
        --master spark://spark-master:7077 \
        --deploy-mode client \
        --driver-memory 4g \
        --executor-memory 4g \
        --executor-cores 2 \
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
        --conf spark.hadoop.fs.s3a.impl=org.apache.hadoop.fs.s3a.S3AFileSystem \
        --conf spark.hadoop.fs.s3a.connection.ssl.enabled=false \
        --conf spark.sql.parquet.enableVectorizedReader=false \
        --conf spark.sql.catalog.minio.warehouse=s3a://datalake/ \
        --conf spark.hadoop.aws.region=us-east-1 \
        --conf "spark.driver.extraJavaOptions=-javaagent:/opt/jmx-exporter/jmx_prometheus_javaagent-0.20.0.jar=8085:/opt/jmx-exporter/spark.yml -Drole=driver" \
        --conf "spark.executor.extraJavaOptions=-javaagent:/opt/jmx-exporter/jmx_prometheus_javaagent-0.20.0.jar=7070:/opt/jmx-exporter/spark.yml -Drole=executor" \
        --class com.tadod.App \
        /var/submit/jars/tadod-spark-1.0-jar-with-dependencies.jar \
        "$1" "$2" "$3" "$4"
        echo "[INFO] Spark app finished"
        sleep 15s
}

spark_submit "IcebergCompaction" "/var/ingestion/yellow.properties" $CURRENT_DATE $CURRENT_DATE

spark_submit "IcebergCompaction" "/var/ingestion/green.properties" $CURRENT_DATE $CURRENT_DATE

spark_submit "IcebergCompaction" "/var/ingestion/fhv.properties" $CURRENT_DATE $CURRENT_DATE
