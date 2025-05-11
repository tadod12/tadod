#! /bin/bash

echo "[INFO] Current date: $(date +"%Y-%m-%d %T")"

# Current time
CURRENT_HOUR=$(date +"-H")
CURRENT_DATE=$(date +"%Y-%m-%d")

spark_submit() {
    $SPARK_HOME/bin/spark-submit \
        --master spark://spark-master:7077 \
        --deploy-mode client \
        --driver-memory 1g \
        --executor-memory 1g \
        --executor-cores 1 \
        --num-executors 1 \
        --conf spark.ui.port=4048 \
        --conf spark.ui.prometheus.enabled=true \
        --conf "spark.driver.extraJavaOptions=-javaagent:/opt/jmx-exporter/jmx_prometheus_javaagent-0.20.0.jar=8085:/opt/jmx-exporter/spark.yml -Drole=driver" \
        --conf "spark.executor.extraJavaOptions=-javaagent:/opt/jmx-exporter/jmx_prometheus_javaagent-0.20.0.jar=7070:/opt/jmx-exporter/spark.yml -Drole=executor" \
        --conf spark.sql.catalog.iceberg.s3.endpoint=http://minio1:9000 \
        --conf spark.hadoop.fs.s3a.endpoint=http://minio1:9000 \
        --class com.tadod.App \
        /var/submit/jars/tadod-spark-1.0-jar-with-dependencies.jar \
        "$1" "$2" "$3" "$4"
        echo "[INFO] Spark app finished"
        sleep 1s
}

spark_submit "YellowStream" "/var/ingestion/yellow.properties" $CURRENT_DATE $CURRENT_DATE

# http://localhost:4048/metrics/prometheus/

# docker exec -it spark-worker-1 /bin/bash
# rm /opt/spark/jars/commons-pool2-2.11.1.jar
# cp /var/submit/jars/commons-pool2-2.11.1.jar /opt/spark/jars/commons-pool2-2.11.1.jar

# docker exec -it spark-worker-2 /bin/bash
# docker exec -it spark-worker-3 /bin/bash
