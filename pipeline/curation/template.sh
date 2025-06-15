#! /bin/bash

command="$1"
properties="$2"
date="$3"

echo "[INFO] Date run: $(date +"%Y-%m-%d %T")"

# Current time
CURRENT_HOUR=$(date +"-H")
CURRENT_DATE=$(date +"%Y-%m-%d")
JARS_DIR=$SPARK_HOME/jars

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
        --conf spark.sql.catalog.iceberg=org.apache.iceberg.spark.SparkCatalog \
        --conf spark.sql.catalog.iceberg.type=hive \
        --conf spark.sql.catalog.iceberg.uri=thrift://hive-metastore:9083 \
        --conf spark.sql.catalog.iceberg.io-impl=org.apache.iceberg.aws.s3.S3FileIO \
        --conf spark.sql.catalog.iceberg.s3.endpoint=http://minio1:9000 \
        --conf spark.hadoop.fs.s3a.impl=org.apache.hadoop.fs.s3a.S3AFileSystem \
        --conf spark.hadoop.fs.s3.impl=org.apache.hadoop.fs.s3a.S3AFileSystem \
        --conf spark.hadoop.fs.s3a.access.key=minio \
        --conf spark.hadoop.fs.s3a.secret.key=minio123 \
        --conf spark.hadoop.fs.s3a.endpoint=http://minio1:9000 \
        --conf spark.hadoop.fs.s3a.path.style.access=true \
        --conf spark.hadoop.fs.s3a.aws.credentials.provider=org.apache.hadoop.fs.s3a.SimpleAWSCredentialsProvider \
        --conf spark.sql.parquet.enableVectorizedReader=false \
        --conf spark.sql.catalog.minio.warehouse=s3a://datalake/ \
        --class com.tadod.App \
        /var/submit/jars/tadod-spark-1.0-jar-with-dependencies.jar \
        "$1" "$2" "$3" "$4"
        echo "[INFO] Spark app finished"
        sleep 60s
}

spark_submit "$command" "$properties" "$date"
