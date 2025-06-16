# Docker Quick Deploy

## Prerequisites

- Docker
- Docker Compose
- Git

## Quick Start

1. Clone the repository

```
git clone https://github.com/tadod12/tadod.git
```

2. Run the following command to start

```
# To run in the foreground, use:
docker-compose -f docker-compose.yml up

 # To run in detached mode, use:
docker-compose -f docker-compose.yml up -d
```

3. Access components UI

| Component     | URL                               | Username  | Password  |
|---------------|-----------------------------------|-----------|-----------|
| Kafka         | http://localhost:8080/ui          |           |           |
| Airflow       | http://localhot:8082/home         | airflow   | airflow   |
| MinIO         | http://localhost:9001/minio       | minio     | minio123  |
| Trino         | http://localhost:8889/ui          | admin     |           |
| Superset      | http://localhost:8088/explore     | admin     | admin     |
| Grafana       | http://localhost:3000/dashboards  | admin     | admin     |
| Prometheus    | http://localhost:9090/query       |           |           |
| HistoryServer | http://localhost:18080/           |           |           |

4. For some adjusting:

- Spark: Access `/pipeline/tadod-spark` to change job, configuration, ...
- Airflow: Editing dags at `/dags`
- Prometheus AlertManager: `/docker/prometheus/rules`
- Grafana dashboard: `docker/grafana/provisioning/dashboards`

4. To stop the pipeline, run:

```
docker-compose -f docker-compose.yml down
```

5. To view logs, run:

```
docker logs -f containerID
```
