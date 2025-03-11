FROM apache/spark:3.4.1-scala2.12-java11-python3-ubuntu

USER root

WORKDIR ${SPARK_HOME}
# /opt/spark/

ENV PATH="/opt/spark/sbin:/opt/spark/bin:${PATH}" \
    SPARK_VERSION_SHORT=3.4 \
    SPARK_VERSION=3.4.1 \
    AWS_SDK_VERSION=1.12.262 \
    HADOOP_AWS_VERSION=3.3.4 \
    SCALA_VERSION=2.12 \
    ICEBERG_VERSION=1.3.1 \
    AWS_SDK_BUNDLE_VERSION=2.20.18

# Configure SPARK
RUN apt-get update -y && apt-get install -y curl wget

RUN curl https://repo1.maven.org/maven2/org/apache/hadoop/hadoop-aws/${HADOOP_AWS_VERSION}/hadoop-aws-${HADOOP_AWS_VERSION}.jar -o ${SPARK_HOME}/jars/hadoop-aws-${HADOOP_AWS_VERSION}.jar
RUN curl https://repo1.maven.org/maven2/com/amazonaws/aws-java-sdk-bundle/${AWS_SDK_VERSION}/aws-java-sdk-bundle-${AWS_SDK_VERSION}.jar -o ${SPARK_HOME}/jars/aws-java-sdk-bundle-${AWS_SDK_VERSION}.jar
RUN curl https://repo1.maven.org/maven2/org/apache/spark/spark-hadoop-cloud_2.12/${SPARK_VERSION}/spark-hadoop-cloud_2.12-3.3.0.jar -o ${SPARK_HOME}/jars/spark-hadoop-cloud_2.12-${SPARK_VERSION}.jar

# Iceberg
RUN curl https://repo1.maven.org/maven2/org/apache/iceberg/iceberg-spark-runtime-${SPARK_VERSION_SHORT}_2.12/${ICEBERG_VERSION}/iceberg-spark-runtime-${SPARK_VERSION_SHORT}_2.12-${ICEBERG_VERSION}.jar -o ${SPARK_HOME}/jars/iceberg-spark-runtime-${SPARK_VERSION_SHORT}_2.12-${ICEBERG_VERSION}.jar
RUN curl https://repo1.maven.org/maven2/software/amazon/awssdk/bundle/${AWS_SDK_BUNDLE_VERSION}/bundle-${AWS_SDK_BUNDLE_VERSION}.jar -Lo /opt/spark/jars/aws-bundle-${AWS_SDK_BUNDLE_VERSION}.jar
RUN curl https://repo1.maven.org/maven2/software/amazon/awssdk/url-connection-client/${AWS_SDK_BUNDLE_VERSION}/url-connection-client-${AWS_SDK_BUNDLE_VERSION}.jar -Lo /opt/spark/jars/url-connection-client-${AWS_SDK_BUNDLE_VERSION}.jar

# Kafka
RUN curl https://repo1.maven.org/maven2/org/apache/spark/spark-sql-kafka-0-10_2.12/3.4.1/spark-sql-kafka-0-10_2.12-3.4.1.jar -o ${SPARK_HOME}/jars/spark-sql-kafka-0-10_2.12-3.4.1.jar
RUN curl https://repo1.maven.org/maven2/org/apache/kafka/kafka-clients/3.4.1/kafka-clients-3.4.1.jar -o ${SPARK_HOME}/jars/kafka-clients-3.4.1.jar
RUN curl https://repo1.maven.org/maven2/org/apache/spark/spark-streaming-kafka-0-10_2.12/3.4.1/spark-streaming-kafka-0-10_2.12-3.4.1.jar -o ${SPARK_HOME}/jars/spark-streaming-kafka-0-10_2.12-3.4.1.jar
RUN curl https://repo1.maven.org/maven2/org/apache/commons/commons-pool2/2.8.0/commons-pool2-2.8.0.jar -o ${SPARK_HOME}/jars/commons-pool2-2.8.0.jar
RUN curl https://repo1.maven.org/maven2/org/apache/spark/spark-token-provider-kafka-0-10_2.12/3.4.1/spark-token-provider-kafka-0-10_2.12-3.4.1.jar -o ${SPARK_HOME}/jars/spark-token-provider-kafka-0-10_2.12-3.4.1.jar
