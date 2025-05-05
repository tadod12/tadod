# Prometheus Metrics Description

This document provides descriptions for all metrics available in the Prometheus monitoring system, organized by component.

## JMX Metrics
- **jmx_config_reload_failure_created** - Timestamp when JMX exporter failed to reload its configuration. 
- **jmx_config_reload_failure_total** - Total count of JMX exporter configuration reload failures.
- **jmx_config_reload_success_created** - Timestamp when JMX exporter successfully reloaded its configuration.
- **jmx_config_reload_success_total** - Total count of successful JMX exporter configuration reloads.
- **jmx_exporter_build_info** - Build information about the JMX exporter.
- **jmx_scrape_cached_beans** - Number of beans that were cached during JMX scraping.
- **jmx_scrape_duration_seconds** - Time taken to scrape JMX beans.
- **jmx_scrape_error** - Indicates whether there was an error during JMX scraping (1 for error, 0 for no error).

## JVM Metrics
- **jvm_buffer_pool_capacity_bytes** - Total capacity of JVM buffer pools in bytes.
- **jvm_buffer_pool_used_buffers** - Number of buffers currently used in JVM buffer pools.
- **jvm_buffer_pool_used_bytes** - Amount of memory currently used in JVM buffer pools in bytes.
- **jvm_classes_currently_loaded** - Number of classes currently loaded in the JVM.
- **jvm_classes_loaded_total** - Total number of classes loaded since JVM start.
- **jvm_classes_unloaded_total** - Total number of classes unloaded since JVM start.
- **jvm_gc_collection_seconds_count** - Count of garbage collection operations.
- **jvm_gc_collection_seconds_sum** - Total time spent in garbage collection.
- **jvm_info** - JVM version info.
- **jvm_memory_bytes_committed** - Amount of memory committed to the JVM.
- **jvm_memory_bytes_init** - Initial amount of memory allocated to the JVM.
- **jvm_memory_bytes_max** - Maximum amount of memory that can be allocated to the JVM.
- **jvm_memory_bytes_used** - Amount of memory currently used by the JVM.
- **jvm_memory_objects_pending_finalization** - Number of objects waiting for finalization.
- **jvm_memory_pool_allocated_bytes_created** - Timestamp when memory pool allocation metric was created.
- **jvm_memory_pool_allocated_bytes_total** - Total bytes allocated to a memory pool since JVM start.
- **jvm_memory_pool_bytes_committed** - Amount of memory committed to a specific memory pool.
- **jvm_memory_pool_bytes_init** - Initial amount of memory in a specific memory pool.
- **jvm_memory_pool_bytes_max** - Maximum amount of memory in a specific memory pool.
- **jvm_memory_pool_bytes_used** - Current memory used in a specific memory pool.
- **jvm_memory_pool_collection_committed_bytes** - Memory committed after garbage collection in a memory pool.
- **jvm_memory_pool_collection_init_bytes** - Initial memory after garbage collection in a memory pool.
- **jvm_memory_pool_collection_max_bytes** - Maximum memory after garbage collection in a memory pool.
- **jvm_memory_pool_collection_used_bytes** - Memory used after garbage collection in a memory pool.
- **jvm_threads_current** - Current number of threads in the JVM.
- **jvm_threads_daemon** - Current number of daemon threads in the JVM.
- **jvm_threads_deadlocked** - Number of threads in deadlock state.
- **jvm_threads_deadlocked_monitor** - Number of threads in monitor deadlock state.
- **jvm_threads_peak** - Peak number of threads in the JVM.
- **jvm_threads_started_total** - Total number of threads started since JVM start.
- **jvm_threads_state** - Current number of threads by state.

## Kafka Metrics

### Kafka Admin Client
- **kafka_admin_client_authentication_time_ns_avg** - Average time in nanoseconds for authentication.
- **kafka_admin_client_commit_id_info** - Kafka commit ID information.
- **kafka_admin_client_connection_close_total** - Total number of closed connections.
- **kafka_admin_client_connection_count** - Current number of active connections.
- **kafka_admin_client_connection_creation_total** - Total number of created connections.
- **kafka_admin_client_failed_authentication_total** - Total number of failed authentications.
- **kafka_admin_client_failed_connection_authentications_total** - Total number of failed connection authentications.
- **kafka_admin_client_failed_handshake_total** - Total number of failed handshakes.
- **kafka_admin_client_failed_reauthentication_total** - Total number of failed reauthentications.
- **kafka_admin_client_handshake_time_ns_avg** - Average time in nanoseconds for handshakes.
- **kafka_admin_client_idle_connection_close_total** - Total number of idle connections closed.
- **kafka_admin_client_incoming_byte_total** - Total bytes received.
- **kafka_admin_client_io_ratio** - Ratio of time spent doing I/O.
- **kafka_admin_client_io_time_ns_avg** - Average time in nanoseconds for I/O operations.
- **kafka_admin_client_io_time_ns_total** - Total time in nanoseconds spent in I/O operations.
- **kafka_admin_client_io_wait_ratio** - Ratio of time spent waiting for I/O.
- **kafka_admin_client_io_wait_time_ns_avg** - Average time in nanoseconds waiting for I/O.
- **kafka_admin_client_io_wait_time_ns_total** - Total time in nanoseconds spent waiting for I/O.
- **kafka_admin_client_io_waittime_total** - Total time spent waiting for I/O (legacy metric).
- **kafka_admin_client_iotime_total** - Total time spent in I/O operations (legacy metric).
- **kafka_admin_client_network_io_total** - Total network I/O operations.
- **kafka_admin_client_node_incoming_byte_total** - Total bytes received per node.
- **kafka_admin_client_node_outgoing_byte_total** - Total bytes sent per node.
- **kafka_admin_client_node_request_latency_avg** - Average request latency per node.
- **kafka_admin_client_node_request_size_avg** - Average request size per node.
- **kafka_admin_client_node_request_total** - Total requests sent per node.
- **kafka_admin_client_node_response_total** - Total responses received per node.
- **kafka_admin_client_outgoing_byte_total** - Total bytes sent.
- **kafka_admin_client_reauthentication_latency_avg** - Average reauthentication latency.
- **kafka_admin_client_request_size_avg** - Average request size.
- **kafka_admin_client_request_total** - Total number of requests sent.
- **kafka_admin_client_response_total** - Total number of responses received.
- **kafka_admin_client_reverse_connection_added_total** - Total reverse connections added.
- **kafka_admin_client_reverse_connection_removed_total** - Total reverse connections removed.
- **kafka_admin_client_select_total** - Total number of select operations performed.
- **kafka_admin_client_start_time_seconds** - Time when the client was started.
- **kafka_admin_client_successful_authentication_no_reauth_total** - Total successful authentications without reauthentication.
- **kafka_admin_client_successful_authentication_total** - Total successful authentications.
- **kafka_admin_client_successful_connection_authentications_total** - Total successful connection authentications.
- **kafka_admin_client_successful_reauthentication_total** - Total successful reauthentications.
- **kafka_admin_client_timed_out_authentication_total** - Total number of authentication timeouts.
- **kafka_admin_client_version_info** - Kafka client version information.

### Kafka Connect
- **kafka_connect_authentication_time_ns_avg** - Average authentication time for Connect clients.
- **kafka_connect_commit_id_info** - Kafka Connect commit ID information.
- **kafka_connect_connection_close_total** - Total connections closed in Connect.
- **kafka_connect_connection_count** - Current active connections in Connect.
- **kafka_connect_connection_creation_total** - Total connections created in Connect.
- **kafka_connect_coordinator_assigned_connectors** - Number of connectors assigned to this worker.
- **kafka_connect_coordinator_assigned_tasks** - Number of tasks assigned to this worker.
- **kafka_connect_coordinator_failed_rebalance_total** - Total failed rebalance operations.
- **kafka_connect_coordinator_heartbeat_total** - Total heartbeats sent.
- **kafka_connect_coordinator_join_time_avg** - Average time to join the group.
- **kafka_connect_coordinator_join_total** - Total number of group joins.
- **kafka_connect_coordinator_last_heartbeat_seconds_ago** - Seconds since last heartbeat.
- **kafka_connect_coordinator_last_rebalance_seconds_ago** - Seconds since last rebalance.
- **kafka_connect_coordinator_rebalance_latency_avg** - Average rebalance latency.
- **kafka_connect_coordinator_rebalance_latency_total** - Total rebalance latency.
- **kafka_connect_coordinator_rebalance_total** - Total number of rebalances.
- **kafka_connect_coordinator_sync_time_avg** - Average sync time.
- **kafka_connect_coordinator_sync_total** - Total number of syncs.
- **kafka_connect_worker_connector_count** - Number of connectors running in this worker.
- **kafka_connect_worker_connector_startup_attempts_total** - Total connector startup attempts.
- **kafka_connect_worker_connector_startup_failure_percentage** - Percentage of connector startup failures.
- **kafka_connect_worker_connector_startup_failure_total** - Total connector startup failures.
- **kafka_connect_worker_connector_startup_success_percentage** - Percentage of connector startup successes.
- **kafka_connect_worker_connector_startup_success_total** - Total connector startup successes.
- **kafka_connect_worker_task_count** - Number of tasks running in this worker.
- **kafka_connect_worker_task_startup_attempts_total** - Total task startup attempts.
- **kafka_connect_worker_task_startup_failure_percentage** - Percentage of task startup failures.
- **kafka_connect_worker_task_startup_failure_total** - Total task startup failures.
- **kafka_connect_worker_task_startup_success_percentage** - Percentage of task startup successes.
- **kafka_connect_worker_task_startup_success_total** - Total task startup successes.

### Kafka Consumer
- **kafka_consumer_authentication_time_ns_avg** - Average authentication time for consumers.
- **kafka_consumer_commit_id_info** - Consumer commit ID information.
- **kafka_consumer_commit_sync_time_ns_total** - Total time for synchronous commits.
- **kafka_consumer_committed_time_ns_total** - Total time for commit operations.
- **kafka_consumer_connection_close_total** - Total connections closed by consumer.
- **kafka_consumer_connection_count** - Current active connections for consumer.
- **kafka_consumer_connection_creation_total** - Total connections created by consumer.
- **kafka_consumer_coordinator_commit_latency_avg** - Average commit latency through coordinator.
- **kafka_consumer_coordinator_commit_total** - Total commits through coordinator.
- **kafka_consumer_coordinator_failed_rebalance_total** - Total failed rebalances for consumer.
- **kafka_consumer_coordinator_heartbeat_total** - Total heartbeats sent by consumer.
- **kafka_consumer_coordinator_join_time_avg** - Average time to join consumer group.
- **kafka_consumer_coordinator_join_total** - Total number of consumer group joins.
- **kafka_consumer_coordinator_last_heartbeat_seconds_ago** - Seconds since last heartbeat from consumer.
- **kafka_consumer_coordinator_last_rebalance_seconds_ago** - Seconds since last consumer group rebalance.
- **kafka_consumer_fetch_manager_bytes_consumed_total** - Total bytes consumed.
- **kafka_consumer_fetch_manager_fetch_latency_avg** - Average fetch latency.
- **kafka_consumer_fetch_manager_fetch_size_avg** - Average fetch size.
- **kafka_consumer_fetch_manager_fetch_throttle_time_avg** - Average fetch throttle time.
- **kafka_consumer_fetch_manager_fetch_total** - Total number of fetches.
- **kafka_consumer_fetch_manager_records_consumed_total** - Total records consumed.
- **kafka_consumer_fetch_manager_records_lag** - Current record lag for consumer.
- **kafka_consumer_fetch_manager_records_lag_avg** - Average record lag for consumer.
- **kafka_consumer_fetch_manager_records_lead** - Current record lead for consumer.
- **kafka_consumer_fetch_manager_records_lead_avg** - Average record lead for consumer.
- **kafka_consumer_fetch_manager_records_per_request_avg** - Average records per request.
- **kafka_consumer_last_poll_seconds_ago** - Seconds since last poll operation.
- **kafka_consumer_poll_idle_ratio_avg** - Average ratio of time consumer is idle between polls.
- **kafka_consumer_time_between_poll_avg** - Average time between polls.

### Kafka Producer
- **kafka_producer_authentication_time_ns_avg** - Average producer authentication time.
- **kafka_producer_batch_size_avg** - Average batch size for producer.
- **kafka_producer_batch_split_total** - Total number of batch splits.
- **kafka_producer_buffer_available_bytes** - Available buffer bytes.
- **kafka_producer_buffer_exhausted_total** - Total buffer exhaustion events.
- **kafka_producer_buffer_total** - Total buffer size.
- **kafka_producer_bufferpool_wait_ratio** - Ratio of time spent waiting for buffer pool.
- **kafka_producer_bufferpool_wait_time_ns_total** - Total time waiting for buffer pool in nanoseconds.
- **kafka_producer_compression_rate_avg** - Average compression rate.
- **kafka_producer_metadata_age** - Age of producer metadata in seconds.
- **kafka_producer_produce_throttle_time_avg** - Average throttle time for produce requests.
- **kafka_producer_record_error_total** - Total record errors.
- **kafka_producer_record_queue_time_avg** - Average time records spend in queue.
- **kafka_producer_record_retry_total** - Total record retries.
- **kafka_producer_record_send_total** - Total records sent.
- **kafka_producer_record_size_avg** - Average record size.
- **kafka_producer_records_per_request_avg** - Average records per request.
- **kafka_producer_request_latency_avg** - Average request latency.
- **kafka_producer_request_size_avg** - Average request size.
- **kafka_producer_requests_in_flight** - Current requests in flight.
- **kafka_producer_topic_byte_total** - Total bytes produced per topic.
- **kafka_producer_topic_compression_rate** - Compression rate per topic.
- **kafka_producer_topic_record_error_total** - Total record errors per topic.
- **kafka_producer_topic_record_retry_total** - Total record retries per topic.
- **kafka_producer_topic_record_send_total** - Total records sent per topic.
- **kafka_producer_waiting_threads** - Number of threads waiting on producer.

### Kafka Server
- **kafka_server_brokertopicmetrics_bytesin_total** - Total bytes received by broker per topic.
- **kafka_server_brokertopicmetrics_bytesout_total** - Total bytes sent by broker per topic.
- **kafka_server_brokertopicmetrics_bytesrejected_total** - Total bytes rejected by broker per topic.
- **kafka_server_brokertopicmetrics_failedfetchrequests_total** - Total failed fetch requests.
- **kafka_server_brokertopicmetrics_failedproducerequests_total** - Total failed produce requests.
- **kafka_server_brokertopicmetrics_messagesin_total** - Total messages received by broker per topic.
- **kafka_server_brokertopicmetrics_totalfetchrequests_total** - Total fetch requests.
- **kafka_server_brokertopicmetrics_totalproducerequests_total** - Total produce requests.
- **kafka_server_replicamanager_underreplicatedpartitions** - Number of under-replicated partitions.
- **kafka_server_replicamanager_partitioncount** - Total number of partitions.
- **kafka_server_kafkaserver_brokerstate** - Current broker state (0=Not Running, 1=Starting, 2=Recovery, 3=Running).

### Kafka Cluster
- **kafka_cluster_partition_atminisr** - Indicates if partition is at minimum in-sync replica count.
- **kafka_cluster_partition_insyncreplicascount** - Number of in-sync replicas for a partition.
- **kafka_cluster_partition_laststableoffsetlag** - Lag of last stable offset.
- **kafka_cluster_partition_replicascount** - Total number of replicas for a partition.
- **kafka_cluster_partition_underminisr** - Indicates if partition is under minimum in-sync replica count.
- **kafka_cluster_partition_underreplicated** - Indicates if partition is under-replicated.

### Kafka Controller
- **kafka_controller_activecontrollercount** - Count of active controllers (should be 1).
- **kafka_controller_controllerstate** - State of the controller.
- **kafka_controller_globaltopiccount** - Total number of topics.
- **kafka_controller_globalpartitioncount** - Total number of partitions.
- **kafka_controller_offlinepartitionscount** - Number of offline partitions.

## Spark Metrics

### Spark Driver
- **spark_driver_BlockManager_disk_diskSpaceUsed_MB_type_gauges** - Disk space used by BlockManager in MB.
- **spark_driver_BlockManager_memory_maxMem_MB_type_gauges** - Maximum memory for BlockManager in MB.
- **spark_driver_BlockManager_memory_memUsed_MB_type_gauges** - Memory used by BlockManager in MB.
- **spark_driver_BlockManager_memory_remainingMem_MB_type_gauges** - Remaining memory for BlockManager in MB.
- **spark_driver_DAGScheduler_job_activeJobs_type_gauges** - Number of active jobs.
    spark_driver_DAGScheduler_job_activeJobs_type_gauges{app_id="app-20250427125834-0000", group="spark", instance="spark-master:8085", job="spark-driver"}
    spark_driver_DAGScheduler_job_activeJobs_type_gauges{app_id="app-20250427133322-0001", group="spark", instance="spark-master:8085", job="spark-driver"}
    spark_driver_DAGScheduler_job_activeJobs_type_gauges{app_id="app-20250427133927-0002", group="spark", instance="spark-master:8085", job="spark-driver"}
- **spark_driver_DAGScheduler_job_allJobs_type_gauges** - Total number of jobs.
    park_driver_DAGScheduler_job_allJobs_type_gauges{app_id="app-20250427125834-0000", group="spark", instance="spark-master:8085", job="spark-driver"}
    spark_driver_DAGScheduler_job_allJobs_type_gauges{app_id="app-20250427133322-0001", group="spark", instance="spark-master:8085", job="spark-driver"}
    spark_driver_DAGScheduler_job_allJobs_type_gauges{app_id="app-20250427133927-0002", group="spark", instance="spark-master:8085", job="spark-driver"}
- **spark_driver_DAGScheduler_stage_failedStages_type_gauges** - Number of failed stages.
    spark_driver_DAGScheduler_stage_failedStages_type_gauges{app_id="app-20250427125834-0000", group="spark", instance="spark-master:8085", job="spark-driver"}
- **spark_driver_DAGScheduler_stage_runningStages_type_gauges** - Number of running stages.
    spark_driver_DAGScheduler_stage_runningStages_type_gauges{app_id="app-20250427125834-0000", group="spark", instance="spark-master:8085", job="spark-driver"}
    spark_driver_DAGScheduler_stage_runningStages_type_gauges{app_id="app-20250427133322-0001", group="spark", instance="spark-master:8085", job="spark-driver"}
    spark_driver_DAGScheduler_stage_runningStages_type_gauges{app_id="app-20250427133927-0002", group="spark", instance="spark-master:8085", job="spark-driver"}
- **spark_driver_DAGScheduler_stage_waitingStages_type_gauges** - Number of waiting stages.
    spark_driver_DAGScheduler_stage_waitingStages_type_gauges{app_id="app-20250427133322-0001", group="spark", instance="spark-master:8085", job="spark-driver"}
    spark_driver_DAGScheduler_stage_waitingStages_type_gauges{app_id="app-20250427133927-0002", group="spark", instance="spark-master:8085", job="spark-driver"}
- **spark_driver_CodeGenerator_compilationTime_type_histograms_total** - Time spent in code compilation.
    spark_driver_CodeGenerator_compilationTime_type_histograms_total{app_id="app-20250427133322-0001", group="spark", instance="spark-master:8085", job="spark-driver"}
    spark_driver_CodeGenerator_compilationTime_type_histograms_total{app_id="app-20250427133927-0002", group="spark", instance="spark-master:8085", job="spark-driver"}
- **spark_driver_LiveListenerBus_numEventsPosted_type_counters_total** - Number of events posted to LiveListenerBus.

### Spark Master and Worker
- **spark_master_aliveWorkers_type_gauges** - Number of alive workers.
    spark_master_aliveWorkers_type_gauges{group="spark", instance="master", job="spark-master"} - done
- **spark_master_apps_type_gauges** - Number of applications.
    spark_master_apps_type_gauges{group="spark", instance="master", job="spark-master"} - done
- **spark_master_waitingApps_type_gauges** - Number of waiting applications.
    spark_master_waitingApps_type_gauges{group="spark", instance="master", job="spark-master"}
- **spark_master_workers_type_gauges** - Total number of workers.
    spark_master_workers_type_gauges{group="spark", instance="master", job="spark-master"} - done
- **spark_worker_coresFree_type_gauges** - Number of free cores on worker.
    spark_worker_coresFree_type_gauges{group="spark", instance="spark-worker-1:7071", job="spark-worker"}
    spark_worker_coresFree_type_gauges{group="spark", instance="spark-worker-2:7072", job="spark-worker"}
- **spark_worker_coresUsed_type_gauges** - Number of used cores on worker.
    spark_worker_coresUsed_type_gauges{group="spark", instance="spark-worker-1:7071", job="spark-worker"}
    spark_worker_coresUsed_type_gauges{group="spark", instance="spark-worker-2:7072", job="spark-worker"}
- **spark_worker_executors_type_gauges** - Number of executors on worker.
    spark_worker_executors_type_gauges{group="spark", instance="spark-worker-1:7071", job="spark-worker"}
    spark_worker_executors_type_gauges{group="spark", instance="spark-worker-2:7072", job="spark-worker"}
- **spark_worker_memFree_MB_type_gauges** - Free memory on worker in MB.
    spark_worker_memFree_MB_type_gauges{group="spark", instance="spark-worker-1:7071", job="spark-worker"}
    spark_worker_memFree_MB_type_gauges{group="spark", instance="spark-worker-2:7072", job="spark-worker"}
- **spark_worker_memUsed_MB_type_gauges** - Used memory on worker in MB.
    spark_worker_memUsed_MB_type_gauges{group="spark", instance="spark-worker-1:7071", job="spark-worker"}
    spark_worker_memUsed_MB_type_gauges{group="spark", instance="spark-worker-2:7072", job="spark-worker"}

## ZooKeeper Metrics
- **zookeeper_AuthFailedCount** - Number of failed authentication attempts.
- **zookeeper_AvgRequestLatency** - Average request latency.
- **zookeeper_MaxRequestLatency** - Maximum request latency.
- **zookeeper_MinRequestLatency** - Minimum request latency.
- **zookeeper_OutstandingRequests** - Number of outstanding requests.
- **zookeeper_PacketsReceived** - Number of packets received.
- **zookeeper_PacketsSent** - Number of packets sent.
- **zookeeper_NumAliveConnections** - Number of active connections.
- **zookeeper_InMemoryDataTree_NodeCount** - Number of nodes in in-memory data tree.
- **zookeeper_InMemoryDataTree_WatchCount** - Number of watches set on in-memory data tree.
- **zookeeper_Leader** - Indicates if this server is the leader (1) or not (0).
- **zookeeper_QuorumSize** - Size of the ZooKeeper quorum.
- **zookeeper_SyncLimit** - ZooKeeper sync limit configuration.
- **zookeeper_TickTime** - ZooKeeper tick time configuration.
- **zookeeper_MaxSessionTimeout** - Maximum session timeout setting.
- **zookeeper_MinSessionTimeout** - Minimum session timeout setting.
- **zookeeper_DataDirSize** - Size of data directory in bytes.
- **zookeeper_LogDirSize** - Size of log directory in bytes.

## Process Metrics
- **process_cpu_seconds_total** - Total user and system CPU time spent in seconds.
- **process_max_fds** - Maximum number of open file descriptors.
- **process_open_fds** - Number of open file descriptors.
- **process_resident_memory_bytes** - Resident memory size in bytes.
- **process_start_time_seconds** - Start time of the process since unix epoch in seconds.
- **process_virtual_memory_bytes** - Virtual memory size in bytes.

## Scrape Metrics
- **scrape_duration_seconds** - Duration of the scrape.
- **scrape_samples_post_metric_relabeling** - Number of samples remaining after metric relabeling was applied.
- **scrape_samples_scraped** - Number of samples the target exposed.
- **scrape_series_added** - Number of new series in this scrape.
- **up** - 1 if the instance is healthy, 0 if the scrape failed.
