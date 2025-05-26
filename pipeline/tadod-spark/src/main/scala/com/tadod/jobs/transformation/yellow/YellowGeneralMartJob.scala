package com.tadod.jobs.transformation.yellow

import com.tadod.jobs.transformation.BaseMartJob
import org.apache.spark.sql.functions._
import org.apache.spark.sql.{DataFrame, SaveMode}

class YellowGeneralMartJob(configPath: String, dateRun: String) extends BaseMartJob(configPath, dateRun) {

  import spark.implicits._

  override protected def getJobName: String = "GeneralMart"

  override protected def createMart(sourceDf: DataFrame): DataFrame = {
    val targetDf = sourceDf
      .withColumn("record_hour", hour($"tpep_pickup_datetime"))
      .withColumn("record_date", to_date($"tpep_pickup_datetime"))
      .withColumn("record_weekday", date_format($"tpep_pickup_datetime", "E"))
      .withColumn("record_month", month($"tpep_pickup_datetime"))
      .withColumn("record_year", year($"tpep_pickup_datetime"))
      .groupBy("record_hour", "record_date", "record_weekday", "record_month", "record_year")
      .agg(
        count("*").as("total_records"),
        sum($"total_amount").as("total_amount"),
        sum($"trip_distance").as("total_distance"),
        sum($"passenger_count").as("total_passengers"))
      .select(
        "record_hour",
        "record_date",
        "record_weekday",
        "record_month",
        "record_year",
        "total_records",
        "total_amount",
        "total_distance",
        "total_passengers"
      )

    targetDf
  }

  override protected def writeToIceberg(targetDf: DataFrame): Unit = {
    //    targetDf.show(truncate = false)
    targetDf.write
      .format("iceberg")
      .mode(SaveMode.Append)
      .save("iceberg.curated.mart_general")
  }
}
