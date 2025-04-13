package com.tadod.jobs.transformation

import org.apache.spark.sql.{DataFrame, SaveMode}
import org.apache.spark.sql.functions._

class MartVendorJob(configPath: String, dateRun: String) extends BaseMartJob(configPath) {

  import spark.implicits._

  override protected def getJobName: String = "MartVendorJob"

  override protected def createMart(sourceDf: DataFrame): DataFrame = {
    val rawDf = spark.read
      .format("iceberg")
      .load("iceberg.raw.yellow")

    val invalidDf = rawDf
      .withColumn("record_date", to_date($"tpep_pickup_datetime"))
      .where(
        $"record_date" === to_date(lit(dateRun)) &&
          col("vendor_id").isNotNull && (
          col("passenger_count").isNull ||
            col("passenger_count") <= 0 ||
            col("trip_distance").isNull ||
            col("trip_distance") <= 0 ||
            col("store_and_fwd_flag").isNull ||
            col("pu_location_id").isNull ||
            col("do_location_id").isNull ||
            col("fare_amount").isNull ||
            col("total_amount").isNull
          )
      )
      .groupBy("record_date", "vendor_id")
      .agg(count("*").as("total_invalid_records"))


    val tmpDf = sourceDf
      .withColumn("record_date", to_date($"tpep_pickup_datetime"))
      .where($"record_date" === to_date(lit(dateRun)))
      .groupBy("record_date", "vendor_id")
      .agg(
        count("*").as("total_valid_records"),
        sum(when($"store_and_fwd_flag" === "Y", 1).otherwise(0)).as("total_delay_records"),
        sum(when($"store_and_fwd_flag" === "N", 1).otherwise(0)).as("total_ontime_records")
      )

    tmpDf.join(invalidDf, Seq("record_date", "vendor_id"))
      .withColumn("record_week", weekofyear($"record_date"))
      .withColumn("record_month", month($"record_date"))
      .withColumn("record_year", year($"record_date"))
      .withColumn("vendor_name",
        when($"vendor_id" === 1, "Creative Mobile Technologies, LLC")
          .when($"vendor_id" === 2, "Curb Mobility, LLC")
          .when($"vendor_id" === 6, "Myle Technologies Inc")
          .when($"vendor_id" === 7, "Helix"))
      .select(
        "record_date",            // DATE
        "record_week",            // INT
        "record_month",           // INT
        "record_year",            // INT
        "vendor_id",              // INT
        "vendor_name",            // VARCHAR
        "total_valid_records",    // BIGINT
        "total_invalid_records",  // BIGINT
        "total_delay_records",    // BIGINT
        "total_ontime_records"    // BIGINT
      )
  }

  override protected def writeToIceberg(targetDf: DataFrame): Unit = {
    targetDf.write
      .format("iceberg")
      .mode(SaveMode.Append)
      .save("iceberg.curated.mart_vendor")
  }
}
