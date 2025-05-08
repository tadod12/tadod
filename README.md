<div align="center">

# TADOD - Data Pipeline for TLC Trip Record Data

</div>

## Overview


## Architecture

![Architecture](assets/draft-170425.png)

### Service Ports

| Service           | Port                                  | BASE IMAGE                                            |
|-------------------|---------------------------------------|-------------------------------------------------------|
| PostgreSQL        | `5432:5432`                           | `postgres:14`                                         |
| MinIO             | `9000:9000` `9001:9001`               | `minio/minio:latest`                                  |
| Hive-Metastore    | `9083:9083`                           | `starburstdata/hive:3.1.2-e.18`                       |
| Trino             | `8889:8889`                           | `trinodb/trino:425`                                   |
| Spark-Master      | `7077:7077` `8084:8084`               | `apache/spark:3.4.1-scala2.12-java11-python3-ubuntu`  |
| Superset          | `8088:8088`                           | `apache/superset:latest`                              |
| Airflow-Webserver | `8082:8080`                           | `apache/airflow:2.10.5-python3.12`                    |
| Prometheus        | `9090:9090`                           | `prom/prometheus:v2.47.2`                             |
| Alert-Manager     | `19093:9093`                          | `prom/alertmanager:v0.26.0`                           |
| Grafana           | `3000:3000`                           | `grafana/grafana:5.2.1`                               |
| Zookeeper         | `32181` `42181` `52181`               | `confluentinc/cp-zookeeper:7.5.1`                     |
| Kafka             | `9092:9092` `9093:9093` `9094:9094`   | `confluentinc/cp-kafka:7.5.1`                         |
| Schema-Registry   | `8081:8081`                           | `confluentinc/cp-schema-registry:7.5.1`               |
| Kafka-Connect     | `8083:8083`                           | `cnfldemos/cp-server-connect-datagen:0.6.2-7.5.0`     |
| Kafka-UI          | `8080:8080`                           | `provectuslabs/kafka-ui:latest`                       |

### Metrics Expose Ports

## Configuration

### Kafka

- Message Expire Time: `30s` (for running backfill)
- Bootstrap Servers: `kafka-1:29092`, `kafka-2:29093`, `kafka-3:29094`
- Topics: `yellow`, `green`, `fhv`

## Project Structure

```
📦assets
 ┣ 📂data - (gitignore) Store example datasets
 ┃ ┣ 📂fhv - For Hire Vehicle Trips Data
 ┃ ┣ 📂fhvhv - High Volume For Hire Vehicle Trips Data
 ┃ ┣ 📂green - Green trips Data
 ┃ ┗ 📂yellow - Yellow Trips Data
 ┣ 📂glossary
 ┃ ┗ ℹ️info.xlsx - Logic for Data Marts
 ┣ 📂map
 ┃ ┗ 👀taxi_zone_lookup.csv - Zone Lookup for Location ID
 ┣ 📂research - Note some stuff
 ┣ 🖼️draft-*.png - (draft) System Architectures
 📦dags - Store Python scripts for Airflow
 ┣ 📂daily - Daily Interval Jobs
 ┃ ┣ 🐍daily_vendor_stats.py
 ┃ ┗ 🐍iceberg_compaction.py
 ┣ 📂monthly - Monthly Interval Jobs
 ┃ ┣ 🐍crawler.py
 ┃ ┣ 🐍dag_ingestion.py
 ┃ ┗ 🐍producer.py
 ┣ 📂weekly - Weekly Interval Jobs
 ┣ 🐍test_*.py - Test scripts
 📦docker
 ┣ 📂airflow
 ┃ ┣ 📂config - Mounted
 ┃ ┣ 📂plugins - Mounted
 ┃ ┣ 🐋.dockerignore
 ┃ ┣ 🐋Dockerfile - Build Airflow image
 ┣ 📂debezium
 ┃ ┣ 🐋Dockerfile - Build Debezium image
 ┃ ┣ 🐗register.*.json - Debezium Kafka connector definition
 ┃ ┣ 💲maven-downloader.sh - Scripts for downloading packages
 ┣ 📂grafana
 ┃ ┣ 📂provisioning
 ┃ ┃ ┣ 📂datasources
 ┃ ┃ ┃ ┣ 🐦‍🔥prometheus_ds.yml - Data source definition
 ┃ ┣ 🔐login_config
 ┃ ┣ 🧼*.json - Dashboard configuration
 ┣ 📂hive-metastore
 ┃ ┣ 🐋Dockerfile - Build Hive-Metastore image
 ┃ ┣ 🍟metastore-site.xml - obsolescent
 ┣ 📂jmx-exporter
 ┃ ┣ 🎒jmx_prometheus_javaagent-0.20.0.jar
 ┃ ┣ 🥩*.yml - Metrics export template
 ┣ 📂kafka - nothing just f*cking around
 ┣ 📂mysql
 ┣ 📂postgres
 ┣ 📂prometheus
 ┣ 📂schemas
 ┣ 📂spark
 ┣ 📂superset
 ┣ 📂trino
```

## License

This project is licensed under the [Apache License](./LICENSE)
