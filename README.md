<div align="center">

# TADOD - Data Pipeline for TLC Trip Record Data

</div>

## Table of Contents

- [Overview](#overview)
- [Architecture](#architecture)
- [License](#license)

## Overview
The data pipeline for processing and storing TLC Trip Record Data leverages a Medallion Architecture within a Lakehouse environment, emphasizing data quality and reliability. This architecture is designed to efficiently handle large-scale data ingestion, transformation, and querying, while ensuring data integrity and governance

## Architecture

![Architecture](assets/architecture.png)

### Data Ingestion Layer (Bronze Layer)
- **Debezium** captures change data from **MySQL**, which acts as an external system storing public transportation service data, and streams the data to **Apache Kafka**
- **Apache Spark Structured Streaming** consumes data from Kafka and writes it in **Iceberg** table format to **MinIO**, with **Hive Metastore** handling metadata management

### Data Transformation Layer (Silver Layer)
- **Apache Spark** processes the raw data from **Iceberg** tables, performing data cleansing, normalization, and enrichment
- Data quality checks are implemented using **OpenMetadata**, which provides data profiling, lineage tracking, and anomaly detection
- The transformed data is stored back into Iceberg tables, managed by Hive Metastore for efficient querying
- **Apache Airflow** orchestrates and schedules data processing tasks, ensuring workflow automation and dependency management

### Data Serving Layer (Gold Layer) - not figure out yet

### Data Quality Focus

- **Schema Validation**: Ensures data consistency and format compliance during ingestion
- **Data Profiling & Anomaly Detection**: OpenMetadata monitors data metrics and detects outliers or missing data
- **Audit Trail & Lineage Tracking**: Tracks data flow and transformation history for traceability and compliance
- **Data Deduplication & Cleansing**: Spark-based jobs handle data normalization and remove duplicates

## License

This project is licensed under the [Apache License](./LICENSE)
