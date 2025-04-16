package com.tadod.models.schema

import org.apache.spark.sql.types._

object FHVSchema {
  val schema: StructType = StructType(Seq(
    StructField("dispatching_base_num", StringType),
    StructField("pickup_datetime", StringType),
    StructField("dropoff_datetime", StringType),
    StructField("pu_location_id", IntegerType),
    StructField("do_location_id", IntegerType),
    StructField("sr_flag", IntegerType),
    StructField("affiliated_base_number", StringType)
  ))
}
