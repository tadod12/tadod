package com.tadod.jobs.transformation

import com.tadod.config.Config
import com.tadod.jobs.base.BaseJob
import com.tadod.models.database.IcebergWriterConfig

import org.apache.log4j.{Level, LogManager, Logger}
import org.apache.spark.sql.functions._

import java.time.{LocalDate, YearMonth}
import java.time.format.DateTimeFormatter

class YellowCleaningJob(configPath: String, dateRun: String) extends BaseJob {

  @transient lazy val LOGGER: Logger = LogManager.getLogger(getClass.getName)
  LOGGER.setLevel(Level.DEBUG)

  val jobName = "YellowCleaning"
  loadConfig(configPath, jobName)

  import spark.implicits._

  def clean(): Unit = {
    try {
      val icebergConfig = loadIcebergConfig()

      val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
      val currentDate = LocalDate.parse(dateRun, formatter)
      val previousMonth = YearMonth.from(currentDate).minusMonths(1)
      val startOfPreviousMonth = previousMonth.atDay(1).format(formatter)
      val endOfPreviousMonth = previousMonth.atEndOfMonth().format(formatter)

      val rawDf = spark.read
        .format("iceberg")
        .load(s"${icebergConfig.catalog}.${icebergConfig.schema}.${icebergConfig.table}")
        .filter(
          col("tpep_pickup_datetime").between(startOfPreviousMonth, endOfPreviousMonth) ||
            col("tpep_dropoff_datetime").between(startOfPreviousMonth, endOfPreviousMonth)
        )

      LOGGER.debug(s"Number of raw records in $startOfPreviousMonth-$endOfPreviousMonth: ${rawDf.count()}")

      // Cleaning

    } catch {
      case e: Exception =>
        LOGGER.debug(s"Spark app shutdown: $e")
        e.printStackTrace()
    } finally spark.stop()
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