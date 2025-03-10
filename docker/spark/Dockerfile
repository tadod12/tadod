FROM apache/spark:3.4.1-scala2.12-java11-python3-ubuntu

USER root

# WORKDIR ${SPARK_HOME}

# ENV PATH="/opt/spark/sbin:/opt/spark/bin:${PATH}" \
#     SPARK_VERSION_SHORT=3.4 \
#     SPARK_VERSION=3.4.2 \
#     AWS_SDK_VERSION=1.12.262 \
#     HADOOP_AWS_VERSION=3.3.4

# # Configure SPARK
# RUN apt-get update -y && apt-get install -y curl wget
# RUN curl https://repo1.maven.org/maven2/org/apache/hadoop/hadoop-aws/${HADOOP_AWS_VERSION}/hadoop-aws-${HADOOP_AWS_VERSION}.jar -o ${SPARK_HOME}/jars/hadoop-aws-${HADOOP_AWS_VERSION}.jar
# RUN curl https://repo1.maven.org/maven2/com/amazonaws/aws-java-sdk-bundle/${AWS_SDK_VERSION}/aws-java-sdk-bundle-${AWS_SDK_VERSION}.jar -o ${SPARK_HOME}/jars/aws-java-sdk-bundle-${AWS_SDK_VERSION}.jar
# RUN curl https://repo1.maven.org/maven2/org/apache/spark/spark-hadoop-cloud_2.12/${SPARK_VERSION}/spark-hadoop-cloud_2.12-3.3.0.jar -o ${SPARK_HOME}/jars/spark-hadoop-cloud_2.12-${SPARK_VERSION}.jar

# # Configure Iceberg
# ENV ICEBERG_VERSION=1.3.1 \
#     AWS_SDK_BUNDLE_VERSION=2.20.18

# RUN curl https://repo1.maven.org/maven2/org/apache/iceberg/iceberg-spark-runtime-${SPARK_VERSION_SHORT}_2.12/${ICEBERG_VERSION}/iceberg-spark-runtime-${SPARK_VERSION_SHORT}_2.12-${ICEBERG_VERSION}.jar -o ${SPARK_HOME}/jars/iceberg-spark-runtime-${SPARK_VERSION_SHORT}_2.12-${ICEBERG_VERSION}.jar
# RUN curl https://repo1.maven.org/maven2/software/amazon/awssdk/bundle/${AWS_SDK_BUNDLE_VERSION}/bundle-${AWS_SDK_BUNDLE_VERSION}.jar -Lo /opt/spark/jars/aws-bundle-${AWS_SDK_BUNDLE_VERSION}.jar
# RUN curl https://repo1.maven.org/maven2/software/amazon/awssdk/url-connection-client/${AWS_SDK_BUNDLE_VERSION}/url-connection-client-${AWS_SDK_BUNDLE_VERSION}.jar -Lo /opt/spark/jars/url-connection-client-${AWS_SDK_BUNDLE_VERSION}.jar


# # Sử dụng image chính thức của Apache Spark
# FROM apache/spark:3.4.1-scala2.12-java11-python3-ubuntu

# # Cài đặt các thư viện cần thiết
# RUN apt-get update && apt-get install -y curl vim unzip wget && \
#     rm -rf /var/lib/apt/lists/*

# # Cài đặt thư viện Spark-Kafka và Iceberg
# RUN mkdir -p /opt/spark/jars

# # Thêm Kafka connector để Spark có thể đọc Kafka topics
# RUN wget -qO /opt/spark/jars/spark-sql-kafka-0-10_2.12-3.4.1.jar \
#     https://repo1.maven.org/maven2/org/apache/spark/spark-sql-kafka-0-10_2.12/3.4.1/spark-sql-kafka-0-10_2.12-3.4.1.jar

# # Thêm Kafka Client
# RUN wget -qO /opt/spark/jars/kafka-clients-3.3.1.jar \
#     https://repo1.maven.org/maven2/org/apache/kafka/kafka-clients/3.3.1/kafka-clients-3.3.1.jar

# # Thêm Iceberg connector để Spark có thể đọc/ghi Iceberg tables
# RUN wget -qO /opt/spark/jars/iceberg-spark-runtime-3.4_2.12-1.3.1.jar \
#     https://repo1.maven.org/maven2/org/apache/iceberg/iceberg-spark-runtime-3.4_2.12/1.3.1/iceberg-spark-runtime-3.4_2.12-1.3.1.jar

# # Đặt biến môi trường
# ENV SPARK_HOME=/opt/spark
# ENV PATH="${SPARK_HOME}/bin:${PATH}"

# # Copy file config Spark
# COPY spark-defaults.conf /opt/spark/conf/spark-defaults.conf

# WORKDIR /opt/spark

# tôi muốn viết Dockerfile của Spark rồi sau đó viết docker-compose cho cụm spark với mục đích dùng cụm này consum message từ kafka và lưu vào iceberg, đồng thời cũng thực hiện các job transform iceberg, với image base là apache/spark:3.4.1-scala2.12-java11-python3-ubuntu