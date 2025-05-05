<div align="center">

# TADOD - Data Pipeline for TLC Trip Record Data

</div>

## Table of Contents

- [Overview](#overview)
- [Architecture](#architecture)
- [License](#license)

## Overview


## Architecture

![Architecture](assets/draft-170425.png)

| Service           | Port                                  | BASE IMAGE            |
|-------------------|---------------------------------------|-----------------------|
| PostgreSQL        | `5432:5432`                           | `postgres:14`         |
| MinIO             | `9000:9000` `9001:9001`               |                       |
| Hive-Metastore    | `9083:9083`                           |                       |
| Trino             | `8889:8889`                           |
| Spark-Master      | `7077:7077` `8084:8084`               |
| Superset          | `8088:8088`                           |
| Airflow-Webserver | `8082:8080`                           |
| Prometheus        | `9090:9090`                           |
| Alert-Manager     | `19093:9093`                          |
| Grafana           | `3000:3000`                           |
| Zookeeper         | `32181` `42181` `52181`               |
| Kafka             | `9092:9092` `9093:9093` `9094:9094`   |
| Schema-Registry   | `8081:8081`                           |
| Kafka-Connect     | `8083:8083`                           |
| Kafka-UI          | `8080:8080`                           |

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
 ┃ ┗ 📜info.xlsx - Logic for Data Marts
 ┣ 📂map
 ┃ ┗ 📜taxi_zone_lookup.csv - Zone Lookup for Location ID
 ┣ 📂research - Note some stuff
 ┣ 📜draft-*.png - (draft) System Architectures
 📦dags - Store Python scripts for Airflow
 ┣ 📂daily - Daily Interval Jobs
 ┃ ┣ 📜daily_vendor_stats.py
 ┃ ┗ 📜iceberg_compaction.py
 ┣ 📂monthly - Monthly Interval Jobs
 ┃ ┣ 📜crawler.py
 ┃ ┣ 📜dag_ingestion.py
 ┃ ┗ 📜producer.py
 ┣ 📂weekly - Weekly Interval Jobs
 ┣ 📜test_*.py - Test scripts
 📦docker
 ┣ 📂airflow
 ┃ ┣ 📂config - Mounted
 ┃ ┣ 📂plugins - Mounted
 ┃ ┣ 🐋.dockerignore
 ┃ ┣ 🐋Dockerfile - Build Airflow image
 ┣ 📂debezium
 ┃ ┣ 🐋Dockerfile - Build Debezium image
 ┃ ┣ 
 ┃ ┣ 
 ┃ ┣ 
 ┃ ┣ 
 ┃ ┣ 💲maven-downloader.sh - Scripts for downloading packages
```

## License

This project is licensed under the [Apache License](./LICENSE)
