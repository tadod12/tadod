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
        --num-executors 1 \
        --conf spark.sql.catalog.iceberg.s3.endpoint=http://minio1:9000 \
        --conf spark.hadoop.fs.s3a.endpoint=http://minio1:9000 \
        --class com.tadod.App \
        /var/submit/jars/tadod-spark-1.0-jar-with-dependencies.jar \
        "$1" "$2" "$3" "$4"
        echo "[INFO] Spark app finished"
        sleep 15s
}

spark_submit "IcebergCompaction" "/var/ingestion/yellow.properties" $CURRENT_DATE $CURRENT_DATE

# spark_submit "IcebergCompaction" "/var/ingestion/green.properties" $CURRENT_DATE $CURRENT_DATE

# spark_submit "IcebergCompaction" "/var/ingestion/fhv.properties" $CURRENT_DATE $CURRENT_DATE
