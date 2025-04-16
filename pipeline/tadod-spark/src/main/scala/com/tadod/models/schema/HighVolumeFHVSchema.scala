package com.tadod.models.schema

import org.apache.spark.sql.types._

object HighVolumeFHVSchema {
  val schema:StructType = StructType(Seq(
    StructField("hvfhs_license_num", StringType),
    StructField("dispatching_base_num", StringType),
    StructField("originating_base_num", StringType),
    StructField("request_datetime", StringType),
    StructField("on_scene_datetime", StringType),
    StructField("pickup_datetime", StringType),
    StructField("dropoff_datetime", StringType),
    StructField("pu_location_id", IntegerType),
    StructField("do_location_id", IntegerType),
    StructField("trip_miles", DoubleType),
    StructField("trip_time", IntegerType),
    StructField("base_passenger_fare", DoubleType),
    StructField("tolls", DoubleType),
    StructField("bcf", DoubleType),
    StructField("sales_tax", DoubleType),
    StructField("congestion_surcharge", DoubleType),
    StructField("airport_fee", DoubleType),
    StructField("tips", DoubleType),
    StructField("driver_pay", DoubleType),
    StructField("shared_request_flag", StringType),
    StructField("shared_match_flag", StringType),
    StructField("access_a_ride_flag", StringType),
    StructField("wav_request_flag", StringType),
    StructField("wav_match_flag", StringType)
  ))
}
