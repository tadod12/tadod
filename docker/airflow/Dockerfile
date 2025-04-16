FROM apache/airflow:latest-python3.12

USER root
RUN apt-get update && \
    apt-get install -y --no-install-recommends openjdk-17-jre-headless && \
    apt-get autoremove -yqq --purge && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# Set JAVA_HOME environment variable
ENV JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64

RUN mkdir -p /opt/airflow/dags /opt/airflow/config /opt/airflow/plugins /opt/airflow/logs \
            /var/ingestion /var/cleaning /var/curation /var/jars /var/submit/jars /var/map /var/data && \
    chown -R 50000:0 /opt/airflow /var/ingestion /var/cleaning /var/curation /var/jars \
            /var/submit/jars /var/map /var/data && \
    chmod -R u+rwX /opt/airflow /var/ingestion /var/cleaning /var/curation /var/jars \
            /var/submit/jars /var/map /var/data

USER 50000

# Install dependencies
RUN pip install --no-cache-dir \
    apache-airflow \
    apache-airflow-providers-apache-spark \
    pyspark \
    pandas \
    requests \
    apache-airflow-providers-postgres \
    pyarrow \
    beautifulsoup4 \
    lxml
