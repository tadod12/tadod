package com.tadod.jobs.transformation.green

import com.tadod.config.Config
import com.tadod.jobs.base.BaseJob
import com.tadod.models.database.IcebergWriterConfig
import org.apache.log4j.{Level, LogManager, Logger}
import org.apache.spark.sql.SaveMode
import org.apache.spark.sql.functions.{col, when}

import java.time.LocalDate
import java.time.format.DateTimeFormatter

class GreenCleaningJob(configPath: String, dateRun: String) extends BaseJob {

  @transient lazy val LOGGER: Logger = LogManager.getLogger(getClass.getName)
  LOGGER.setLevel(Level.DEBUG)

  val jobName = "GreenCleaning"
  loadConfig(configPath, jobName)

  def execute(): Unit = {
    try {
      val icebergConfig = loadIcebergConfig()

      val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
      val currentDate = LocalDate.parse(dateRun, formatter)
      val previousDate = currentDate.minusDays(1).format(formatter)

      val rawDf = spark.read
        .format("iceberg")
        .load(s"${icebergConfig.catalog}.${icebergConfig.schema}.${icebergConfig.table}")
        .filter(col("lpep_pickup_datetime").equalTo(previousDate))

      LOGGER.debug(s"Number of raw records on $previousDate: ${rawDf.count()}\n")

      // Cleaning
      val cleanDf = rawDf
        .na.drop(Seq(
          "vendor_id",
          "lpep_pickup_datetime",
          "lpep_dropoff_datetime",
          "store_and_fwd_flag",
          "pu_location_id",
          "do_location_id",
          "passenger_count",
          "trip_distance",
          "fare_amount",
          "total_amount",
          "trip_type"))
        .na.fill(Map(
          "rate_code_id" -> 99,
          "payment_type" -> 5,
          "extra" -> 0,
          "mta_tax" -> 0,
          "tip_amount" -> 0,
          "tolls_amount" -> 0,
          "improvement_surcharge" -> 0,
          "improvement_surcharge" -> 0,
          "congestion_surcharge" -> 0,
          "ehail_fee" -> 0))
        .where(
          col("vendor_id").isin(1, 2, 6) &&
            col("passenger_count") > 0 &&
            col("trip_distance") > 0 &&
            col("rate_code_id").isin(1, 2, 3, 4, 5, 6, 99) &&
            col("store_and_fwd_flag").isin("Y", "N") &&
            col("payment_type").isin(0, 1, 2, 3, 4, 5, 6) &&
            col("trip_type").isin(1, 2))
        .withColumn("fare_amount", when(col("fare_amount") < 0, 0).otherwise(col("fare_amount")))
        .withColumn("extra", when(col("extra") < 0, 0).otherwise(col("extra")))
        .withColumn("mta_tax", when(col("mta_tax") < 0, 0).otherwise(col("mta_tax")))
        .withColumn("tip_amount", when(col("tip_amount") < 0, 0).otherwise(col("tip_amount")))
        .withColumn("tolls_amount", when(col("tolls_amount") < 0, 0).otherwise(col("tolls_amount")))
        .withColumn("improvement_surcharge", when(col("improvement_surcharge") < 0, 0).otherwise(col("improvement_surcharge")))
        .withColumn("total_amount", when(col("total_amount") < 0, 0).otherwise(col("total_amount")))
        .withColumn("congestion_surcharge", when(col("congestion_surcharge") < 0, 0).otherwise(col("congestion_surcharge")))
        .withColumn("ehail_fee", when(col("ehail_fee") < 0, 0).otherwise(col("ehail_fee")))

      LOGGER.debug(s"After cleaning job, number of records on $previousDate: ${cleanDf.count()}\n")
      cleanDf.show(5, truncate = false)

      cleanDf.write
        .format("iceberg")
        .mode(SaveMode.Append)
        .save("iceberg.clean.green")
    }
  }

  private def loadIcebergConfig(): IcebergWriterConfig = {
    try {
      val config = IcebergWriterConfig(
        catalog = Config.get("iceberg.catalog"),
        schema = Config.get("iceberg.schema"),
        table = Config.get("iceberg.table"),
        partitionCols = Some(Config.get("iceberg.partition.columns"))
      )
      LOGGER.info(
        s"""
           |Loaded Iceberg config:
           |Catalog: ${config.catalog}
           |Schema: ${config.schema}
           |Table: ${config.table}
           |Partition Columns: ${config.partitionCols.getOrElse("none")}
           |""".stripMargin)
      config
    } catch {
      case e: Exception =>
        LOGGER.error("Failed to load Iceberg configuration", e)
        throw e
    }
  }
}
