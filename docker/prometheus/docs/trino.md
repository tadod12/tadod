# Hướng Dẫn Giám Sát Metrics Trino

## Metrics Sức Khỏe Server

| Metric | Mô Tả | Ngưỡng Cảnh Báo | Lý Do | Giải Pháp |
|--------|-------------|-------------------|---------|-----------|
| `trino_metadata_name_discoverynodemanager_activenodecount` | Số lượng node đang hoạt động | Giảm đột ngột | Node không khả dụng | Kiểm tra log node và khởi động lại nếu cần |
| `trino_metadata_name_discoverynodemanager_inactivenodecount` | Số lượng node không hoạt động | > 0 | Node bị lỗi hoặc mất kết nối | Kiểm tra kết nối mạng và sức khỏe node |
| `trino_metadata_name_discoverynodemanager_shuttingdownnodecount` | Số lượng node đang tắt | > 0 | Node đang trong quá trình tắt | Giám sát quá trình tắt node |

## Metrics Hiệu Suất Query

| Metric | Mô Tả | Ngưỡng Cảnh Báo | Lý Do | Giải Pháp |
|--------|-------------|-------------------|---------|-----------|
| `trino_execution_name_querymanager_queuedqueries` | Số lượng query đang xếp hàng | > 10 | Hệ thống quá tải | Mở rộng cluster hoặc tối ưu query |
| `trino_execution_name_querymanager_runningqueries` | Số lượng query đang chạy | > 20 | Tải hệ thống cao | Mở rộng cluster hoặc tối ưu query |
| `trino_execution_name_sqltaskmanager_failedtasks` | Số lượng task thất bại | > 0 | Lỗi xử lý query | Kiểm tra log và cấu hình task |

## Metrics Sử Dụng Tài Nguyên

| Metric | Mô Tả | Ngưỡng Cảnh Báo | Lý Do | Giải Pháp |
|--------|-------------|-------------------|---------|-----------|
| `trino_memory_name_clustermemorymanager_clustermemorybytes` | Tổng bộ nhớ cluster | > 80% | Thiếu bộ nhớ | Mở rộng bộ nhớ hoặc tối ưu query |
| `trino_memory_name_clustermemorymanager_clustertotalmemoryreservation` | Tổng bộ nhớ đã cấp phát | > 80% | Sử dụng bộ nhớ cao | Tối ưu query hoặc mở rộng bộ nhớ |
| `trino_memory_name_clustermemorymanager_querieskilledduetooutofmemory` | Số query bị kill do hết bộ nhớ | > 0 | Thiếu bộ nhớ | Tối ưu query hoặc mở rộng bộ nhớ |

## Metrics Task Execution

| Metric | Mô Tả | Ngưỡng Cảnh Báo | Lý Do | Giải Pháp |
|--------|-------------|-------------------|---------|-----------|
| `trino_execution_executor_name_taskexecutor_blockedsplits` | Số lượng split bị block | > 0 | Tắc nghẽn xử lý | Kiểm tra tài nguyên và tối ưu query |
| `trino_execution_executor_name_taskexecutor_runningsplits` | Số lượng split đang chạy | > 100 | Tải cao | Mở rộng cluster hoặc tối ưu query |
| `trino_execution_executor_name_taskexecutor_waitingsplits` | Số lượng split đang chờ | > 50 | Tắc nghẽn xử lý | Kiểm tra tài nguyên và tối ưu query |

## Metrics Memory Pool

| Metric | Mô Tả | Ngưỡng Cảnh Báo | Lý Do | Giải Pháp |
|--------|-------------|-------------------|---------|-----------|
| `trino_memory_type_memorypool_name_general_freebytes` | Bộ nhớ trống | < 20% | Thiếu bộ nhớ | Mở rộng bộ nhớ hoặc tối ưu query |
| `trino_memory_type_memorypool_name_general_reservedbytes` | Bộ nhớ đã cấp phát | > 80% | Sử dụng bộ nhớ cao | Tối ưu query hoặc mở rộng bộ nhớ |
| `trino_memory_type_memorypool_name_general_maxbytes` | Bộ nhớ tối đa | > 90% | Gần giới hạn bộ nhớ | Mở rộng bộ nhớ hoặc tối ưu query |

## Thực Hành Tốt Cho Việc Giám Sát

1. Thiết lập cảnh báo cho tất cả metrics quan trọng
2. Giám sát xu hướng theo thời gian
3. Thiết lập dashboard cho giám sát thời gian thực
4. Cấu hình thời gian lưu trữ metrics phù hợp
5. Đánh giá định kỳ các ngưỡng cảnh báo
6. Ghi lại tất cả phản hồi cảnh báo
7. Lập kế hoạch mở rộng dựa trên metrics

## Các Vấn Đề Thường Gặp Và Giải Pháp

1. Query Chậm
   - Tối ưu query
   - Mở rộng cluster
   - Kiểm tra cấu hình resource groups

2. Thiếu Bộ Nhớ
   - Tối ưu query
   - Mở rộng bộ nhớ
   - Điều chỉnh cấu hình memory pool

3. Node Không Khả Dụng
   - Kiểm tra sức khỏe node
   - Xác minh kết nối mạng
   - Kiểm tra log node

4. Task Thất Bại
   - Kiểm tra log task
   - Xác minh cấu hình task
   - Kiểm tra tài nguyên hệ thống

5. Tắc Nghẽn Xử Lý
   - Tối ưu query
   - Mở rộng cluster
   - Điều chỉnh cấu hình executor