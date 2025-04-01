create schema iceberg.clean;

select max(date_diff('day', tpep_pickup_datetime, tpep_dropoff_datetime))
from iceberg.raw.yellow y;