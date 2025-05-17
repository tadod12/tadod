# Hướng Dẫn Giám Sát Metrics Kafka

## Metrics Sức Khỏe Broker

| Metric | Mô Tả | Ngưỡng Cảnh Báo | Lý Do | Giải Pháp |
|--------|-------------|-------------------|---------|-----------|
| `kafka_server_kafkaserver_brokerstate` | Trạng thái hiện tại của broker (0=Không chạy, 1=Đang khởi động, 2=Đang phục hồi từ tắt không an toàn, 3=Đang chạy như broker, 4=Đang chạy như controller, 5=Đang chờ tắt có kiểm soát, 6=Broker đang tắt) | != 3 | Broker không ở trạng thái chạy bình thường | Kiểm tra log broker và khởi động lại nếu cần |
| `kafka_server_replicamanager_underreplicatedpartitions` | Số lượng partition bị thiếu replica | > 0 | Độ trễ replication hoặc lỗi broker | Kiểm tra kết nối mạng, dung lượng ổ đĩa và sức khỏe broker |
| `kafka_server_replicamanager_offlinereplicacount` | Số lượng replica offline | > 0 | Replica không khả dụng | Kiểm tra sức khỏe broker và kết nối mạng |
| `kafka_server_replicamanager_atminisrpartitioncount` | Số lượng partition ở mức ISR tối thiểu | > 0 | Một số partition có số lượng replica tối thiểu | Giám sát và đảm bảo hệ số replication đủ |

## Metrics Hiệu Suất

| Metric | Mô Tả | Ngưỡng Cảnh Báo | Lý Do | Giải Pháp |
|--------|-------------|-------------------|---------|-----------|
| `kafka_server_brokertopicmetrics_bytesin_total` | Tổng số byte nhận được | Giảm đột ngột | Vấn đề mạng hoặc producer | Kiểm tra mạng và sức khỏe producer |
| `kafka_server_brokertopicmetrics_bytesout_total` | Tổng số byte gửi đi | Giảm đột ngột | Vấn đề mạng hoặc consumer | Kiểm tra mạng và sức khỏe consumer |
| `kafka_server_brokertopicmetrics_failedproducerequests_total` | Số lượng request produce thất bại | > 0 | Vấn đề producer hoặc quá tải broker | Kiểm tra cấu hình producer và tài nguyên broker |
| `kafka_server_brokertopicmetrics_failedfetchrequests_total` | Số lượng request fetch thất bại | > 0 | Vấn đề consumer hoặc quá tải broker | Kiểm tra cấu hình consumer và tài nguyên broker |

## Metrics Độ Trễ Consumer

| Metric | Mô Tả | Ngưỡng Cảnh Báo | Lý Do | Giải Pháp |
|--------|-------------|-------------------|---------|-----------|
| `kafka_server_fetcherlagmetrics_consumerlag` | Độ trễ consumer theo partition | > 1000 message hoặc > 1 giờ | Consumer xử lý quá chậm | Mở rộng consumer hoặc tối ưu xử lý |
| `kafka_consumer_consumer_fetch_manager_metrics_records_lag` | Độ trễ nhóm consumer | > 1000 message hoặc > 1 giờ | Nhóm consumer xử lý quá chậm | Mở rộng nhóm consumer hoặc tối ưu xử lý |

## Metrics Sử Dụng Tài Nguyên

| Metric | Mô Tả | Ngưỡng Cảnh Báo | Lý Do | Giải Pháp |
|--------|-------------|-------------------|---------|-----------|
| `kafka_server_kafkaserver_linux_disk_read_bytes` | Thông lượng đọc ổ đĩa | > 80% dung lượng ổ đĩa | Tải đọc cao | Cân nhắc thêm broker hoặc tối ưu lưu trữ |
| `kafka_server_kafkaserver_linux_disk_write_bytes` | Thông lượng ghi ổ đĩa | > 80% dung lượng ổ đĩa | Tải ghi cao | Cân nhắc thêm broker hoặc tối ưu lưu trữ |
| `kafka_server_kafkarequesthandlerpool_requesthandleravgidle_percent` | Phần trăm thời gian rảnh của request handler | < 20% | Tải request cao | Mở rộng broker hoặc tối ưu xử lý request |

## Metrics Mạng

| Metric | Mô Tả | Ngưỡng Cảnh Báo | Lý Do | Giải Pháp |
|--------|-------------|-------------------|---------|-----------|
| `kafka_server_brokertopicmetrics_bytesrejected_total` | Số byte bị từ chối | > 0 | Throttling mạng hoặc quá tải broker | Kiểm tra cấu hình mạng và tài nguyên broker |
| `kafka_server_brokertopicmetrics_replicationbytesin_total` | Số byte replication nhận vào | Giảm đột ngột | Vấn đề replication | Kiểm tra mạng và sức khỏe broker |
| `kafka_server_brokertopicmetrics_replicationbytesout_total` | Số byte replication gửi đi | Giảm đột ngột | Vấn đề replication | Kiểm tra mạng và sức khỏe broker |

## Metrics Controller

| Metric | Mô Tả | Ngưỡng Cảnh Báo | Lý Do | Giải Pháp |
|--------|-------------|-------------------|---------|-----------|
| `kafka_controller_kafkacontroller_offlinepartitionscount` | Số partition offline | > 0 | Vấn đề controller hoặc lỗi broker | Kiểm tra sức khỏe controller và broker |
| `kafka_controller_kafkacontroller_globalpartitioncount` | Tổng số partition | Thay đổi đột ngột | Thay đổi topic hoặc vấn đề broker | Giám sát thay đổi topic và sức khỏe broker |
| `kafka_controller_controllerstats_leaderelectionrateandtimems` | Tỷ lệ bầu leader | Giá trị cao | Bầu leader thường xuyên | Kiểm tra độ ổn định broker và sức khỏe mạng |

## Thực Hành Tốt Cho Việc Giám Sát

1. Thiết lập cảnh báo cho tất cả metrics quan trọng
2. Giám sát xu hướng theo thời gian
3. Thiết lập dashboard cho giám sát thời gian thực
4. Cấu hình thời gian lưu trữ metrics phù hợp
5. Đánh giá định kỳ các ngưỡng cảnh báo
6. Ghi lại tất cả phản hồi cảnh báo
7. Lập kế hoạch mở rộng dựa trên metrics

## Các Vấn Đề Thường Gặp Và Giải Pháp

1. Độ Trễ Consumer Cao
   - Mở rộng nhóm consumer
   - Tối ưu xử lý consumer
   - Kiểm tra sức khỏe consumer

2. Partition Thiếu Replica
   - Kiểm tra sức khỏe broker
   - Xác minh kết nối mạng
   - Đảm bảo đủ dung lượng ổ đĩa

3. Sử Dụng Tài Nguyên Cao
   - Mở rộng broker
   - Tối ưu cấu hình topic
   - Xem xét số lượng partition

4. Vấn Đề Mạng
   - Kiểm tra cấu hình mạng
   - Giám sát băng thông
   - Xác minh quy tắc tường lửa

5. Vấn Đề Controller
   - Giám sát sức khỏe controller
   - Kiểm tra kết nối ZooKeeper
   - Xem xét log controller