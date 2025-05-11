#!/bin/bash

echo "[INFO] Current date: $(date +"%Y-%m-%d %T")"

# Current time
CURRENT_HOUR=$(date +"%-H")
CURRENT_DATE=$(date +"%Y-%m-%d")

spark_submit() {
    $SPARK_HOME/bin/spark-submit \
        --master local \
        --deploy-mode client \
        --driver-memory 1g \
        --executor-memory 1g \
        --executor-cores 1 \
        --num-executors 1 \
        --jars /var/submit/jars/simpleclient-0.16.0.jar,/var/submit/jars/simpleclient_httpserver-0.16.0.jar \
        --class com.tadod.App \
        /var/submit/jars/tadod-spark-1.0-jar-with-dependencies.jar \
        "$1" "$2" "$3" "$4"
        echo "[INFO] Spark app finished"
        sleep 1s
}

# spark_submit command port bootstrapserver
spark_submit "KafkaOffset" "8086" "kafka-1:29092,kafka-2:29093,kafka-3:29094" $CURRENT_DATE