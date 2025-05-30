# Giám sát Metrics Apache Spark Cluster

Bảng dưới đây liệt kê các metrics quan trọng để giám sát một cụm Apache Spark, ý nghĩa, lý do cần theo dõi, các metrics cần tạo cảnh báo, giới hạn đề xuất, lý do dẫn đến việc chạm giới hạn, và giải pháp khắc phục.

| Metric Name | Ý Nghĩa | Lý Do Quan Trọng | Cần Tạo Alert | Giới Hạn Đề Xuất | Lý Do Dẫn Đến Giới Hạn | Giải Pháp |
|-------------|---------|------------------|---------------|------------------|-------------------------|-----------|
| `spark_driver_BlockManager_disk_diskSpaceUsed_MB_type_gauges` | Dung lượng đĩa (MB) được BlockManager sử dụng để lưu trữ dữ liệu (ví dụ: RDD, shuffle data). | Giúp theo dõi mức sử dụng đĩa của driver, tránh hết dung lượng đĩa dẫn đến lỗi. | Có | > 80% dung lượng đĩa khả dụng (ví dụ: 800 MB nếu tổng là 1000 MB). | Lượng dữ liệu shuffle hoặc RDD lưu trữ trên đĩa quá lớn, cấu hình `spark.shuffle.spill.disk` không tối ưu, hoặc đĩa không được dọn dẹp. | - Tăng dung lượng đĩa cho driver. <br> - Tối ưu shuffle bằng cách giảm số partition (`spark.sql.shuffle.partitions`). <br> - Dọn dẹp dữ liệu cũ bằng cách bật `spark.cleaner.periodicGC.enabled`. |
| `spark_driver_BlockManager_memory_memUsed_MB_type_gauges` | Tổng bộ nhớ (MB) được BlockManager sử dụng để lưu trữ dữ liệu. | Đảm bảo driver không sử dụng quá nhiều bộ nhớ, tránh tràn bộ nhớ. | Có | > 80% bộ nhớ tối đa của BlockManager (ví dụ: 800 MB nếu max là 1000 MB). | Quá nhiều RDD/DataFrame được cache, cấu hình `spark.memory.fraction` hoặc `spark.memory.storageFraction` không hợp lý. | - Giảm lượng dữ liệu cache bằng `unpersist()`. <br> - Tăng bộ nhớ driver (`spark.driver.memory`). <br> - Điều chỉnh `spark.memory.storageFraction` để giới hạn bộ nhớ cache. |
| `spark_driver_BlockManager_memory_remainingMem_MB_type_gauges` | Bộ nhớ (MB) còn lại mà BlockManager có thể sử dụng. | Giúp phát hiện sớm khi bộ nhớ sắp cạn, ảnh hưởng đến hiệu suất caching. | Có | < 20% bộ nhớ tối đa (ví dụ: < 200 MB nếu max là 1000 MB). | Tương tự như trên, cộng thêm việc ứng dụng tạo ra nhiều khối dữ liệu lớn hoặc không giải phóng bộ nhớ cache. | - Tương tự như trên. <br> - Tối ưu mã để giảm kích thước dữ liệu cache. <br> - Sử dụng định dạng dữ liệu nén (Parquet, ORC) để giảm kích thước. |
| `spark_driver_DAGScheduler_job_activeJobs_type_gauges` | Số lượng job đang hoạt động trong DAGScheduler. | Theo dõi tải công việc của cụm, phát hiện tình trạng quá tải job. | Có | > 50 job hoạt động (tùy thuộc vào quy mô cụm). | Ứng dụng gửi quá nhiều job đồng thời, lập lịch kém hiệu quả, hoặc thiếu tài nguyên (CPU, bộ nhớ) để xử lý job. | - Tăng số lượng executor hoặc worker. <br> - Tối ưu mã để giảm số job (kết hợp transformation). <br> - Điều chỉnh `spark.scheduler.mode` thành FAIR để phân bổ tài nguyên công bằng. |
| `spark_driver_DAGScheduler_stage_failedStages_type_gauges` | Số lượng stage thất bại trong DAGScheduler. | Phát hiện các vấn đề trong quá trình thực thi (lỗi dữ liệu, cấu hình sai). | Có | > 0 stage thất bại. | Lỗi dữ liệu đầu vào, cấu hình executor không đủ tài nguyên, lỗi mạng, hoặc mã ứng dụng có bug. | - Kiểm tra log để xác định nguyên nhân (dữ liệu null, out-of-memory). <br> - Tăng tài nguyên executor (`spark.executor.memory`, `spark.executor.cores`). <br> - Sửa lỗi mã hoặc làm sạch dữ liệu đầu vào. |
| `spark_driver_DAGScheduler_stage_runningStages_type_gauges` | Số lượng stage đang chạy trong DAGScheduler. | Giúp đánh giá mức độ bận rộn của cụm, tối ưu hóa tài nguyên. | Không | - | - | - |
| `spark_driver_jvm_heap_usage_type_gauges` | Phần trăm bộ nhớ heap JVM được sử dụng. | Phát hiện nguy cơ tràn heap, gây lỗi OutOfMemoryError. | Có | > 85% (cảnh báo sớm), > 95% (nghiêm trọng). | Ứng dụng tạo nhiều đối tượng lớn, cấu hình `spark.driver.memory` quá thấp, hoặc không giải phóng bộ nhớ đúng cách. | - Tăng heap size driver (`spark.driver.memory`). <br> - Tối ưu mã để giảm kích thước đối tượng (sử dụng cấu trúc dữ liệu hiệu quả). <br> - Bật GC log để phân tích và điều chỉnh tham số GC (`-XX:+UseG1GC`). |
| `spark_driver_jvm_G1_Old_Generation_count_type_gauges` | Số lần garbage collection (GC) trong vùng Old Generation của G1. | GC thường xuyên có thể làm giảm hiệu suất ứng dụng. | Có | > 10 lần GC/phút (tùy ứng dụng). | Quá nhiều đối tượng sống lâu trong heap, cấu hình heap size nhỏ, hoặc mã ứng dụng không tối ưu. | - Tăng heap size hoặc điều chỉnh `spark.memory.fraction`. <br> - Giảm lượng dữ liệu lưu trữ trong RDD/DataFrame. <br> - Sử dụng cấu hình GC tối ưu (`-XX:G1HeapRegionSize`). |
| **spark_driver_jvm_g1_old_generation_time_type_gauges** | Tổng thời gian (ms) dành cho GC trong vùng Old Generation. | Thời gian GC dài gây gián đoạn ứng dụng, ảnh hưởng hiệu suất. | Có | > 500 ms/phút (hoặc 10% thời gian chạy). | Tương tự như trên, cộng thêm việc heap size không đủ hoặc dữ liệu lớn gây áp lực lên GC. | - Tương tự như trên. <br> - Phân tích GC log để tối ưu thời gian thu gom. <br> - Giảm kích thước dữ liệu xử lý mỗi batch. |
| `spark_master_aliveWorkers_type_gauges` | Số lượng worker đang hoạt động và kết nối với Spark Master. | Đảm bảo cụm có đủ worker để xử lý công việc, phát hiện worker mất kết nối. | Có | < 80% số worker mong muốn (ví dụ: < 8 nếu cần 10 worker). | Worker bị crash do thiếu tài nguyên, lỗi mạng, hoặc cấu hình `spark.network.timeout` quá thấp. | - Kiểm tra log worker để xác định nguyên nhân crash. <br> - Tăng tài nguyên worker (CPU, bộ nhớ). <br> - Tăng `spark.network.timeout` (mặc định 120s) nếu mạng không ổn định. |
| `spark_master_apps_type_gauges` | Số lượng ứng dụng đang chạy trên cụm. | Theo dõi tải ứng dụng, tránh quá tải cụm. | Có | > số lượng ứng dụng tối đa cho phép (ví dụ: > 5). | Quá nhiều ứng dụng được gửi đến cụm, lập lịch không hiệu quả, hoặc cụm thiếu tài nguyên. | - Giới hạn số ứng dụng bằng `spark.scheduler.maxRegisteredResourcesWaitingTime`. <br> - Tăng số worker hoặc tài nguyên cụm. <br> - Sử dụng chế độ FAIR scheduling. |
| `spark_master_waitingapps_type_gauges` | Số lượng ứng dụng đang chờ được lên lịch. | Phát hiện tình trạng tắc nghẽn tài nguyên, cần mở rộng cụm. | Có | > 0 ứng dụng chờ quá 5 phút. | Thiếu tài nguyên (CPU, bộ nhớ) trên worker, hoặc cấu hình tài nguyên ứng dụng không hợp lý. | - Tăng số worker hoặc tài nguyên worker. <br> - Giảm yêu cầu tài nguyên ứng dụng (`spark.executor.memory`, `spark.executor.cores`). <br> - Tối ưu lập lịch bằng `spark.dynamicAllocation.enabled`. |
| `spark_worker_coresFree_type_gauges` | Số lượng CPU core còn trống trên worker. | Đảm bảo worker có đủ CPU để xử lý tác vụ, tránh quá tải. | Có | < 10% tổng số core (ví dụ: < 2 core nếu worker có 20 core). | Quá nhiều executor/task chạy đồng thời, cấu hình `spark.executor.cores` quá cao, hoặc cụm không đủ worker. | - Tăng số worker hoặc core mỗi worker. <br> - Giảm `spark.executor.cores` để phân bổ tài nguyên hợp lý. <br> - Bật `spark.dynamicAllocation.enabled` để tự động điều chỉnh executor. |
| `spark_worker_memFree_MB_type_gauges` | Bộ nhớ (MB) còn trống trên worker. | Phát hiện thiếu bộ nhớ trên worker, ảnh hưởng hiệu suất executor. | Có | < 10% bộ nhớ tổng (ví dụ: < 1000 MB nếu tổng là 10 GB). | Executor sử dụng quá nhiều bộ nhớ, cấu hình `spark.executor.memory` không hợp lý, hoặc cụm thiếu tài nguyên. | - Tăng bộ nhớ worker hoặc thêm worker. <br> - Giảm `spark.executor.memory`. <br> - Tối ưu mã để giảm sử dụng bộ nhớ (sử dụng định dạng nén). |
| **metrics_app_driver_livelistenerbus_listenerprocessingtime_org_apache_spark_heartbeatreceiver_95thpercentile** | Thời gian xử lý ở mức 95% của HeartbeatReceiver (ms). | Phát hiện độ trễ cao trong xử lý heartbeat, do driver quá tải. | Có | > 1000 ms (tùy yêu cầu thời gian thực). | Driver bị quá tải do xử lý quá nhiều sự kiện, cấu hình `spark.driver.cores` hoặc `spark.driver.memory` không đủ. | - Tăng tài nguyên driver (`spark.driver.memory`, `spark.driver.cores`). <br> - Giảm tần suất heartbeat (`spark.executor.heartbeatInterval`). <br> - Tối ưu mã để giảm sự kiện gửi đến driver. |
| `metrics_app_driver_livelistenerbus_queue_appstatus_numdroppedevents_type_counters_total` | Số lượng sự kiện bị bỏ trong hàng đợi AppStatus của LiveListenerBus. | Sự kiện bị bỏ cho thấy driver quá tải, cần tối ưu hoặc tăng tài nguyên. | Có | > 0 sự kiện bị bỏ. | Hàng đợi quá nhỏ (`spark.eventLog.buffer.kb`), driver thiếu tài nguyên, hoặc ứng dụng tạo ra quá nhiều sự kiện. | - Tăng kích thước hàng đợi (`spark.eventLog.buffer.kb`). <br> - Tăng tài nguyên driver. <br> - Giảm số lượng sự kiện bằng cách tắt log chi tiết (`spark.eventLog.enabled` = false nếu không cần). |
| `metrics_app_driver_livelistenerbus_queue_eventlog_numdroppedevents_type_counters_total` | Số lượng sự kiện bị bỏ trong hàng đợi EventLog của LiveListenerBus. | Sự kiện log bị bỏ làm mất dữ liệu giám sát, ảnh hưởng debug. | Có | > 0 sự kiện bị bỏ. | Tương tự như trên, hoặc hệ thống ghi log (HDFS, S3) bị chậm, gây tắc nghẽn. | - Tương tự như trên. <br> - Tăng hiệu suất hệ thống lưu trữ log (sử dụng SSD, tối ưu HDFS/S3). <br> - Giảm tần suất ghi log bằng `spark.eventLog.logBlockUpdates.enabled` = false. |
| `spark_driver_hiveexternalcatalog_hiveclientcalls_type_counters_total` | Tuyệt đối số lần gọi tới Hive client từ HiveExternalCatalog. | Theo dõi hiệu suất truy cập metadata, phát hiện bottleneck trong Hive metastore. | Không | - | - | - |

## Ghi Chú
- **Giới hạn đề xuất** cần được điều chỉnh dựa trên quy mô cụm, khối lượng công việc, và yêu cầu hiệu suất cụ thể. Thử nghiệm thực tế là cần thiết.
- **Tần suất giám sát**: Thu thập metrics mỗi 1-5 phút để phát hiện vấn đề kịp thời.
- **Công cụ giám sát**: Sử dụng Prometheus, Grafana, hoặc Spark UI để trực quan hóa và tạo cảnh báo.
- **Hành động chung**:
  - Kiểm tra log Spark (driver, executor, worker) để xác định nguyên nhân chi tiết.
  - Tối ưu mã ứng dụng (giảm kích thước dữ liệu, sử dụng định dạng nén, tránh lặp lại transformation).
  - Sử dụng phân bổ tài nguyên động (`spark.dynamicAllocation.enabled`) để tự động điều chỉnh tài nguyên.
  - Nếu vấn đề phức tạp, cân nhắc sử dụng công cụ phân tích hiệu suất như Spark UI hoặc các công cụ bên thứ ba (Datadog, New Relic).