package com.tadod.jobs.transformation

import org.apache.spark.sql.{DataFrame, SaveMode}
import org.apache.spark.sql.functions._

class MartPaymentJob(configPath: String, dateRun: String) extends BaseMartJob (configPath){

  import spark.implicits._

  override protected def getJobName: String = "MartPayment"

  override protected def createMart(sourceDf: DataFrame): DataFrame = {
    val targetDf = sourceDf
      .withColumn("record_date", to_date($"tpep_pickup_datetime"))
      .where($"record_date" === dateRun)
      .groupBy("record_date", "payment_type")
      .count().as("total_records")
      .withColumn("record_week", weekofyear($"record_date"))
      .withColumn("record_month", month($"record_date"))
      .withColumn("record_year", year($"record_date"))
      .withColumn("payment_name",
        when($"payment_type" === 0, "Flex Fare trip")
          .when($"payment_type" === 1, "Credit card")
          .when($"payment_type" === 2, "Cash")
          .when($"payment_type" === 3, "No charge")
          .when($"payment_type" === 4, "Dispute")
          .when($"payment_type" === 5, "Unknown")
          .when($"payment_type" === 6, "Voided trip")
          .otherwise("Unknown")
      )

    targetDf
  }

  override protected def writeToIceberg(targetDf: DataFrame): Unit = {
    targetDf.write
      .format("iceberg")
      .mode(SaveMode.Append)
      .save("iceberg.curated.mart_payment")
  }
}
