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
        --jars /fix-this-path/simpleclient-0.16.0.jar,/fix-this-path/simpleclient_httpserver-0.16.0.jar \
        --class com.vnpay.App \
        /var/submit/jars/tadod-spark-1.0-jar-with-dependencies.jar \
        "$1" "$2" "$3" "$4"
        echo "[INFO] Spark app finished"
        sleep 1s
}

spark_submit "SparkOffset" "8086" "/checkpoint-path/" $CURRENT_DATE