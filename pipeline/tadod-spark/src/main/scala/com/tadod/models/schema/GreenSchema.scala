package com.tadod.models.schema

import org.apache.spark.sql.types._

object GreenSchema {
  val schema: StructType = StructType(Seq(
    StructField("vendor_id", IntegerType),
    StructField("lpep_pickup_datetime", StringType),
    StructField("lpep_dropoff_datetime", StringType),
    StructField("passenger_count", DoubleType),
    StructField("trip_distance", DoubleType),
    StructField("rate_code_id", DoubleType),
    StructField("store_and_fwd_flag", StringType),
    StructField("pu_location_id", IntegerType),
    StructField("do_location_id", IntegerType),
    StructField("payment_type", IntegerType),
    StructField("fare_amount", DoubleType),
    StructField("extra", DoubleType),
    StructField("mta_tax", DoubleType),
    StructField("tip_amount", DoubleType),
    StructField("tolls_amount", DoubleType),
    StructField("improvement_surcharge", DoubleType),
    StructField("total_amount", DoubleType),
    StructField("congestion_surcharge", DoubleType),
    StructField("trip_type", IntegerType),
    StructField("cbd_congestion_fee", DoubleType)
  ))
}
