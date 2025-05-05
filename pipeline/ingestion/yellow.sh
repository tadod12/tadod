#! /bin/bash

echo "[INFO] Current date: $(date +"%Y-%m-%d %T")"

# Current time
CURRENT_HOUR=$(date +"-H")
CURRENT_DATE=$(date +"%Y-%m-%d")

spark_submit() {
    $SPARK_HOME/bin/spark-submit \
        --master spark://spark-master:7077 \
        --deploy-mode client \
        --driver-memory 2g \
        --executor-memory 2g \
        --executor-cores 1 \
        --num-executors 2 \
        --conf "spark.driver.extraJavaOptions=-javaagent:/opt/jmx-exporter/jmx_prometheus_javaagent-0.20.0.jar=8085:/opt/jmx-exporter/spark.yml -Drole=driver" \
        --conf "spark.executor.extraJavaOptions=-javaagent:/opt/jmx-exporter/jmx_prometheus_javaagent-0.20.0.jar=7070:/opt/jmx-exporter/spark.yml -Drole=executor" \
        --class com.tadod.App \
        /var/submit/jars/tadod-spark-1.0-jar-with-dependencies.jar \
        "$1" "$2" "$3" "$4"
        echo "[INFO] Spark app finished"
        sleep 1s
}

spark_submit "YellowStream" "/var/ingestion/yellow.properties" $CURRENT_DATE $CURRENT_DATE
