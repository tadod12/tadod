FROM apache/airflow:latest-python3.12

USER root
RUN apt-get update && \
        apt-get install -y --no-install-recommends openjdk-17-jre-headless && \
        apt-get autoremove -yqq --purge && \
        apt-get clean && \
        apt-get install -y sudo && \
        rm -rf /var/lib/apt/lists/*

# Set JAVA_HOME environment variable
ENV JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64

# Grant root privileges to airflow user
RUN echo "airflow ALL=(ALL) NOPASSWD: ALL" >> /etc/sudoers

USER airflow

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
