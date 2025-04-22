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

| Service           | Port                                  |
|-------------------|---------------------------------------|
| PostgreSQL        | `5432:5432`                           |
| MinIO             | `9000:9000` `9001:9001`               |
| Hive-Metastore    | `9083:9083`                           |
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

## License

This project is licensed under the [Apache License](./LICENSE)
