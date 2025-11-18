<div align="center">

# TADOD - Data Lake Monitoring System

</div>

## Overview

This project presents a comprehensive Data Lake Monitoring Module designed to provide end-to-end observability and management capabilities for modern data lake architectures. The system integrates multiple industry-standard technologies to deliver real-time monitoring, alerting, and visualization of data pipeline operations.

## Architecture

![Architecture](assets/draft-160625.png)


| Component	| Description | Technologies |
|-----------|-------------|--------------|
| Data Sources | [NYC TLC taxi trip data](https://www.nyc.gov/site/tlc/about/tlc-trip-record-data.page) | Parquet files |
| Ingestion Layer | Data crawling, message brokering, and streaming | Kafka, Spark Streaming |
| Processing Layer | Data cleaning, transformation, and mart creation | Spark SQL, Iceberg |
| Storage Layer	| Object storage and metadata management | MinIO (S3-compatible), Hive Metastore |
| Query Layer | SQL query engine and visualization | Trino, Superset |
| Orchestration | Workflow management and scheduling | Airflow |
| Monitoring | Metrics collection, alerting, and visualization | Prometheus, Alertmanager, Grafana |

## Monitoring Endpoints

![Monitoring](assets/docs/monitoring.png)

## Quickstart

Follow this [INSTRUCTIONS](assets/docs/instructions.md)
Create an issue for any problem, may be I can help ^^

## References
- [Setting Up Prometheus & Grafana for Kafka on Docker- Oredata Engineering](https://medium.com/@oredata-engineering/setting-up-prometheus-grafana-for-kafka-on-docker-8a692a45966c)
- [Monitoring Apache Spark Metrics with Prometheus - Bhavya Joshi](https://medium.com/@bhavya.joshi1901/monitoring-apache-spark-metrics-with-prometheus-a2846fe94028)
- [Setting up a Spark standalone cluster on Docker in layman terms - Marin Aglić](https://medium.com/@MarinAgli1/setting-up-a-spark-standalone-cluster-on-docker-in-layman-terms-8cbdc9fdd14b)
- [Zookeeper Setup with Kafka Cluster - Sarah M.](https://medium.com/@mengineer/zookeeper-setup-with-kafka-cluster-part-1-469dc4671198)

## License

This project is licensed under the [Apache License](./LICENSE)
