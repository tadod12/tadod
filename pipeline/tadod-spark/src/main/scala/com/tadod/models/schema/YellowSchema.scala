package com.tadod.models.schema

import org.apache.spark.sql.types._

object YellowSchema {
  // Schema for Kafka message payload
  val schema: StructType = StructType(Seq(
    StructField("payload", StructType(Seq(
      StructField("after", StructType(Seq(
        StructField("id", LongType),
        StructField("vendor_id", IntegerType),
        StructField("tpep_pickup_datetime", StringType),
        StructField("tpep_dropoff_datetime", StringType),
        StructField("passenger_count", IntegerType),
        StructField("trip_distance", FloatType),
        StructField("pu_location_id", IntegerType),
        StructField("do_location_id", IntegerType),
        StructField("rate_code_id", IntegerType),
        StructField("store_and_fwd_flag", StringType),
        StructField("payment_type", IntegerType),
        StructField("fare_amount", FloatType),
        StructField("extra", FloatType),
        StructField("mta_tax", FloatType),
        StructField("improvement_surcharge", FloatType),
        StructField("tip_amount", FloatType),
        StructField("tolls_amount", FloatType),
        StructField("total_amount", FloatType),
        StructField("congestion_surcharge", FloatType),
        StructField("airport_fee", FloatType)
      )))
    )))
  ))
}
