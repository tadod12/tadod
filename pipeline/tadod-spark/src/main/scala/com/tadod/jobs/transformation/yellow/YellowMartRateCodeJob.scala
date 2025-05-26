package com.tadod.jobs.transformation.yellow

import com.tadod.jobs.transformation.BaseMartJob
import org.apache.spark.sql.functions._
import org.apache.spark.sql.{DataFrame, SaveMode}

class YellowMartRateCodeJob(configPath: String, dateRun: String) extends BaseMartJob(configPath, dateRun) {

  import spark.implicits._

  override protected def getJobName: String = "MartRateCodeJob"

  override protected def createMart(sourceDf: DataFrame): DataFrame = {
    val targetDf = sourceDf
      .withColumn("record_date", to_date($"tpep_pickup_datetime"))
      //      .where($"record_date" === dateRun)
      .groupBy("record_date", "rate_code_id")
      .agg(count("*").as("total_records"))
      .withColumn("record_week", weekofyear($"record_date"))
      .withColumn("record_month", month($"record_date"))
      .withColumn("record_year", year($"record_date"))
      .withColumn("rate_code_name",
        when($"rate_code_id" === 1, "Standard rate")
          .when($"rate_code_id" === 2, "JFK")
          .when($"rate_code_id" === 3, "Newark")
          .when($"rate_code_id" === 4, "Nassau or Westchester")
          .when($"rate_code_id" === 5, "Negotiated fare")
          .when($"rate_code_id" === 6, "Group ride")
          .otherwise("Null/unknown")
      )

    targetDf.select(
      "record_date", // DATE
      "record_week", // INT
      "record_month", // INT
      "record_year", // INT
      "rate_code_id", // INT
      "rate_code_name", // VARCHAR
      "total_records" // BIGINT
    )
  }

  override protected def writeToIceberg(targetDf: DataFrame): Unit = {
    //    targetDf.show(truncate = false)
    targetDf.write
      .format("iceberg")
      .mode(SaveMode.Append)
      .save("iceberg.curated.mart_ratecode")
  }
}
