package com.tadod.jobs.transformation.yellow

import com.tadod.jobs.transformation.BaseMartJob
import org.apache.spark.sql.functions.{col, date_format, to_timestamp}
import org.apache.spark.sql.{DataFrame, SaveMode}

import java.time.LocalDate
import java.time.format.DateTimeFormatter

class YellowDumpJob(configPath: String, dateRun: String) extends BaseMartJob(configPath, dateRun) {
  LOGGER.info(s"=== YELLOW DUMP JOB on $dateRun ===")
  override protected def getJobName: String = "YellowDump"

  override protected def createMart(sourceDf: DataFrame): DataFrame = {
    spark.emptyDataFrame
  }

  override protected def writeToIceberg(targetDf: DataFrame): Unit = {
    // do nothing
  }

  override def execute(): Unit = {
    LOGGER.info(s"Start $getJobName job")

    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val currentDate = LocalDate.parse(dateRun, formatter)
    val monthStr = currentDate.format(DateTimeFormatter.ofPattern("yyyy-MM"))
    val filePath = s"/var/data/yellow/yellow_tripdata_$monthStr.parquet"

    val df: DataFrame = spark.read
      .parquet(filePath)
      .select(
        col("VendorID").as("vendor_id"),
        col("tpep_pickup_datetime").as("tpep_pickup_datetime"),
        col("tpep_dropoff_datetime").as("tpep_dropoff_datetime"),
        col("passenger_count").cast("int").as("passenger_count"),
        col("trip_distance").as("trip_distance"),
        col("PULocationID").as("pu_location_id"),
        col("DOLocationID").as("do_location_id"),
        col("RatecodeID").cast("int").as("rate_code_id"),
        col("store_and_fwd_flag").as("store_and_fwd_flag"),
        col("payment_type").as("payment_type"),
        col("fare_amount").as("fare_amount"),
        col("extra").as("extra"),
        col("mta_tax").as("mta_tax"),
        col("improvement_surcharge").as("improvement_surcharge"),
        col("tip_amount").as("tip_amount"),
        col("tolls_amount").as("tolls_amount"),
        col("total_amount").as("total_amount"),
        col("congestion_surcharge").as("congestion_surcharge"),
        col("Airport_fee").as("airport_fee")
      )
      .withColumn("tpep_pickup_datetime", to_timestamp(date_format(col("tpep_pickup_datetime"), "yyyy-MM-dd HH:mm:ss"), "yyyy-MM-dd HH:mm:ss"))
      .withColumn("tpep_dropoff_datetime", to_timestamp(date_format(col("tpep_dropoff_datetime"), "yyyy-MM-dd HH:mm:ss"), "yyyy-MM-dd HH:mm:ss"))

    df.write
      .format("iceberg")
      .mode(SaveMode.Append)
      .save("iceberg.raw.yellow")
  }
}
