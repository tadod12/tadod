package com.tadod.jobs.transformation.yellow

import com.tadod.jobs.transformation.BaseMartJob
import org.apache.spark.sql.functions._
import org.apache.spark.sql.{DataFrame, SaveMode}

class YellowMartLocationJob(configPath: String, dateRun: String) extends BaseMartJob(configPath) {

  import spark.implicits._

  override protected def getJobName: String = "MartLocation"

  override protected def createMart(sourceDf: DataFrame): DataFrame = {
    val lookupDf = spark.read
      .format("csv")
      .option("header", value = true)
      .option("inferSchema", value = true)
      .load("file:///var/map/taxi_zone_lookup.csv")

    // LocationID
    // Borough
    // Zone
    // service_zone

    val tempDf = sourceDf
      .withColumn("record_date", to_date($"tpep_pickup_datetime"))
      .where($"record_date" === to_date(lit(dateRun)))
      .groupBy("record_date", "pu_location_id", "do_location_id")
      .agg(count("*").as("total_records"))
      .join(lookupDf, lookupDf("LocationID") === $"pu_location_id", "inner")
      .select(
        col("record_date"),
        col("pu_location_id"),
        col("Borough").as("pu_location_borough"),
        col("Zone").as("pu_location_zone"),
        col("service_zone").as("pu_location_service_zone"),
        col("do_location_id"),
        col("total_records")
      )

    tempDf.join(lookupDf, lookupDf("LocationID") === $"do_location_id", "inner")
      .select(
        col("record_date"),
        col("pu_location_id"),
        col("pu_location_borough"),
        col("pu_location_zone"),
        col("pu_location_service_zone"),
        col("do_location_id"),
        col("Borough").as("do_location_borough"),
        col("Zone").as("do_location_zone"),
        col("service_zone").as("do_location_service_zone"),
        col("total_records")
      )
      .withColumn("record_week", weekofyear($"record_date"))
      .withColumn("record_month", month($"record_date"))
      .withColumn("record_year", year($"record_date"))
      .select(
        "record_date",
        "record_week",
        "record_month",
        "record_year"
      )
  }

  override protected def writeToIceberg(targetDf: DataFrame): Unit = {
    targetDf.write
      .format("iceberg")
      .mode(SaveMode.Append)
      .save("iceberg.curated.mart_location")
  }
}
