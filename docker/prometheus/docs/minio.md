# Hướng Dẫn Giám Sát Metrics MinIO

## Metrics Sức Khỏe Cluster

| Metric | Mô Tả | Ngưỡng Cảnh Báo | Lý Do | Giải Pháp |
|--------|-------------|-------------------|---------|-----------|
| `minio_cluster_health_status` | Trạng thái sức khỏe cluster | != 1 | Cluster không khỏe mạnh | Kiểm tra log và sức khỏe các node |
| `minio_cluster_nodes_offline_total` | Số lượng node offline | > 0 | Node không khả dụng | Kiểm tra kết nối mạng và sức khỏe node |
| `minio_cluster_nodes_online_total` | Số lượng node online | < 2 | Thiếu node | Kiểm tra và khởi động lại node offline |
| `minio_cluster_drive_offline_total` | Số lượng ổ đĩa offline | > 0 | Ổ đĩa không khả dụng | Kiểm tra kết nối và sức khỏe ổ đĩa |

## Metrics Hiệu Suất S3

| Metric | Mô Tả | Ngưỡng Cảnh Báo | Lý Do | Giải Pháp |
|--------|-------------|-------------------|---------|-----------|
| `minio_s3_requests_4xx_errors_total` | Số lượng lỗi 4xx | > 0 | Lỗi client | Kiểm tra request và cấu hình client |
| `minio_s3_requests_errors_total` | Tổng số lỗi | > 0 | Lỗi hệ thống | Kiểm tra log và cấu hình server |
| `minio_s3_requests_inflight_total` | Số request đang xử lý | > 1000 | Tải cao | Mở rộng cluster hoặc tối ưu request |
| `minio_s3_requests_waiting_total` | Số request đang chờ | > 100 | Tắc nghẽn | Kiểm tra tài nguyên và tối ưu xử lý |

## Metrics Sử Dụng Tài Nguyên

| Metric | Mô Tả | Ngưỡng Cảnh Báo | Lý Do | Giải Pháp |
|--------|-------------|-------------------|---------|-----------|
| `minio_cluster_capacity_raw_free_bytes` | Dung lượng trống thô | < 20% | Thiếu dung lượng | Mở rộng dung lượng lưu trữ |
| `minio_cluster_capacity_usable_free_bytes` | Dung lượng trống khả dụng | < 20% | Thiếu dung lượng | Mở rộng dung lượng lưu trữ |
| `minio_node_mem_used_perc` | Phần trăm bộ nhớ đã sử dụng | > 80% | Thiếu bộ nhớ | Mở rộng RAM hoặc tối ưu sử dụng |
| `minio_node_cpu_avg_load15` | Tải CPU trung bình 15 phút | > 80% | Tải CPU cao | Mở rộng CPU hoặc tối ưu tải |

## Metrics Ổ Đĩa

| Metric | Mô Tả | Ngưỡng Cảnh Báo | Lý Do | Giải Pháp |
|--------|-------------|-------------------|---------|-----------|
| `minio_node_drive_errors_ioerror` | Số lỗi I/O | > 0 | Lỗi ổ đĩa | Kiểm tra và thay thế ổ đĩa |
| `minio_node_drive_errors_timeout` | Số lỗi timeout | > 0 | Ổ đĩa chậm | Kiểm tra hiệu suất ổ đĩa |
| `minio_node_drive_perc_util` | Phần trăm sử dụng ổ đĩa | > 80% | Tải ổ đĩa cao | Mở rộng dung lượng hoặc tối ưu I/O |
| `minio_node_drive_latency_us` | Độ trễ ổ đĩa | > 1000 | Ổ đĩa chậm | Kiểm tra và tối ưu ổ đĩa |

## Metrics Mạng

| Metric | Mô Tả | Ngưỡng Cảnh Báo | Lý Do | Giải Pháp |
|--------|-------------|-------------------|---------|-----------|
| `minio_node_if_rx_errors` | Số lỗi nhận | > 0 | Lỗi mạng | Kiểm tra kết nối mạng |
| `minio_node_if_tx_errors` | Số lỗi gửi | > 0 | Lỗi mạng | Kiểm tra kết nối mạng |
| `minio_inter_node_traffic_errors_total` | Tổng lỗi giao tiếp node | > 0 | Lỗi giao tiếp | Kiểm tra mạng nội bộ |
| `minio_inter_node_traffic_dial_errors` | Lỗi kết nối node | > 0 | Lỗi kết nối | Kiểm tra kết nối node |

## Metrics Replication

| Metric | Mô Tả | Ngưỡng Cảnh Báo | Lý Do | Giải Pháp |
|--------|-------------|-------------------|---------|-----------|
| `minio_bucket_replication_proxied_get_requests_failures` | Lỗi replication get | > 0 | Lỗi replication | Kiểm tra cấu hình replication |
| `minio_bucket_replication_proxied_put_tagging_requests_failures` | Lỗi replication put | > 0 | Lỗi replication | Kiểm tra cấu hình replication |
| `minio_node_replication_average_queued_bytes` | Số byte đang chờ replication | > 1GB | Tắc nghẽn replication | Tối ưu replication |
| `minio_node_replication_recent_backlog_count` | Số lượng backlog | > 1000 | Chậm replication | Kiểm tra và tối ưu replication |

## Thực Hành Tốt Cho Việc Giám Sát

1. Thiết lập cảnh báo cho tất cả metrics quan trọng
2. Giám sát xu hướng theo thời gian
3. Thiết lập dashboard cho giám sát thời gian thực
4. Cấu hình thời gian lưu trữ metrics phù hợp
5. Đánh giá định kỳ các ngưỡng cảnh báo
6. Ghi lại tất cả phản hồi cảnh báo
7. Lập kế hoạch mở rộng dựa trên metrics

## Các Vấn Đề Thường Gặp Và Giải Pháp

1. Node Không Khả Dụng
   - Kiểm tra sức khỏe node
   - Xác minh kết nối mạng
   - Kiểm tra log node

2. Ổ Đĩa Lỗi
   - Kiểm tra sức khỏe ổ đĩa
   - Xác minh kết nối ổ đĩa
   - Thay thế ổ đĩa lỗi

3. Thiếu Dung Lượng
   - Mở rộng dung lượng lưu trữ
   - Tối ưu chính sách lưu trữ
   - Xem xét xóa dữ liệu không cần thiết

4. Lỗi Replication
   - Kiểm tra cấu hình replication
   - Xác minh kết nối mạng
   - Tối ưu hiệu suất replication

5. Tải Cao
   - Mở rộng cluster
   - Tối ưu cấu hình
   - Cân bằng tải