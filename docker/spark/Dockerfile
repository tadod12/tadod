FROM apache/spark:3.4.1-scala2.12-java11-python3-ubuntu

USER root

ENV SPARK_HOME="/opt/spark"

WORKDIR ${SPARK_HOME}
# /opt/spark/

ENV PATH="/opt/spark/sbin:/opt/spark/bin:${PATH}"
ENV SPARK_MASTER="spark://spark-master:7077"
ENV SPARK_MASTER_HOST spark-master
ENV SPARK_MASTER_PORT 7077

# SPARK_VERSION_SHORT=3.4
# SPARK_VERSION=3.4.1
# AWS_SDK_VERSION=1.12.262
# HADOOP_AWS_VERSION=3.3.4
# SCALA_VERSION=2.12
# ICEBERG_VERSION=1.4.3
# AWS_SDK_BUNDLE_VERSION=2.20.18

# Configure SPARK
RUN apt-get update -y && apt-get install -y curl wget

RUN curl https://repo1.maven.org/maven2/org/apache/hadoop/hadoop-aws/3.3.4/hadoop-aws-3.3.4.jar -o ${SPARK_HOME}/jars/hadoop-aws-3.3.4.jar
RUN curl https://repo1.maven.org/maven2/com/amazonaws/aws-java-sdk-bundle/1.12.262/aws-java-sdk-bundle-1.12.262.jar -o ${SPARK_HOME}/jars/aws-java-sdk-bundle-1.12.262.jar
RUN curl https://repo1.maven.org/maven2/org/apache/spark/spark-hadoop-cloud_2.12/3.4.1/spark-hadoop-cloud_2.12-3.3.0.jar -o ${SPARK_HOME}/jars/spark-hadoop-cloud_2.12-3.4.1.jar

# Iceberg
RUN curl https://repo1.maven.org/maven2/org/apache/iceberg/iceberg-spark-runtime-3.4_2.12/1.4.3/iceberg-spark-runtime-3.4_2.12-1.4.3.jar -o ${SPARK_HOME}/jars/iceberg-spark-runtime-3.4_2.12-1.4.3.jar
RUN curl https://repo1.maven.org/maven2/software/amazon/awssdk/bundle/2.20.18/bundle-2.20.18.jar -Lo /opt/spark/jars/aws-bundle-2.20.18.jar
RUN curl https://repo1.maven.org/maven2/software/amazon/awssdk/url-connection-client/2.20.18/url-connection-client-2.20.18.jar -Lo /opt/spark/jars/url-connection-client-2.20.18.jar

# Kafka
RUN curl https://repo1.maven.org/maven2/org/apache/spark/spark-sql-kafka-0-10_2.12/3.4.1/spark-sql-kafka-0-10_2.12-3.4.1.jar -o ${SPARK_HOME}/jars/spark-sql-kafka-0-10_2.12-3.4.1.jar
RUN curl https://repo1.maven.org/maven2/org/apache/kafka/kafka-clients/3.4.1/kafka-clients-3.4.1.jar -o ${SPARK_HOME}/jars/kafka-clients-3.4.1.jar
RUN curl https://repo1.maven.org/maven2/org/apache/spark/spark-streaming-kafka-0-10_2.12/3.4.1/spark-streaming-kafka-0-10_2.12-3.4.1.jar -o ${SPARK_HOME}/jars/spark-streaming-kafka-0-10_2.12-3.4.1.jar
RUN curl https://repo1.maven.org/maven2/org/apache/spark/spark-token-provider-kafka-0-10_2.12/3.4.1/spark-token-provider-kafka-0-10_2.12-3.4.1.jar -o ${SPARK_HOME}/jars/spark-token-provider-kafka-0-10_2.12-3.4.1.jar

RUN curl https://repo1.maven.org/maven2/org/apache/commons/commons-pool2/2.8.0/commons-pool2-2.11.1.jar -o ${SPARK_HOME}/jars/commons-pool2-2.11.1.jar
RUN curl https://repo1.maven.org/maven2/io/prometheus/simpleclient/0.16.0/simpleclient-0.16.0.jar -o ${SPARK_HOME}/jars/simpleclient-0.16.0.jar
RUN curl https://repo1.maven.org/maven2/io/prometheus/simpleclient_common/0.16.0/simpleclient_common-0.16.0.jar -o ${SPARK_HOME}/jars/simpleclient_common-0.16.0.jar

COPY ./spark-defaults.conf $SPARK_HOME/conf/spark-defaults.conf
RUN chmod u+x /opt/spark/sbin/* && \
    chmod u+x /opt/spark/bin/* && \
    chmod u+x /opt/spark/jars/*

COPY ./entrypoint.sh .
RUN chmod +x ./entrypoint.sh

ENTRYPOINT ["./entrypoint.sh"]
