#! /bin/bash

command="$1"
properties="$2"
date="$3"

echo "[INFO] Current date: $(date +"%Y-%m-%d %T")"
echo "[INFO] Date run: 2024-02-02"

# Current time
CURRENT_HOUR=$(date +"-H")
CURRENT_DATE=$(date +"%Y-%m-%d")
JARS_DIR=$SPARK_HOME/jars

spark_submit() {
    echo "[INFO] Processing with command: "$1""
    $SPARK_HOME/bin/spark-submit \
        --master spark://spark-master:7077 \
        --deploy-mode client \
        --driver-memory 4g \
        --executor-memory 4g \
        --executor-cores 2 \
        --num-executors 2 \
        --conf spark.driver.extraJavaOptions=-javaagent:/opt/jmx-exporter/jmx_prometheus_javaagent-0.20.0.jar=8084:/opt/jmx-exporter/spark.yml \
        --jars /var/jars/spark-sql-kafka-0-10_2.12-3.4.1.jar,/var/jars/kafka-clients-3.4.1.jar,/var/jars/spark-streaming-kafka-0-10_2.12-3.4.1.jar,/var/jars/commons-pool2-2.11.1.jar,/var/jars/spark-token-provider-kafka-0-10_2.12-3.4.1.jar,/var/jars/aws-java-sdk-bundle-1.12.262.jar,/var/jars/hadoop-aws-3.3.4.jar \
        --class com.tadod.App \
        /var/submit/jars/tadod-spark-1.0-jar-with-dependencies.jar \
        "$1" "$2" "$3" "$4"
        echo "[INFO] Spark app finished"
        sleep 1s
}

spark_submit "$command" "$properties" "$date" "$date"
