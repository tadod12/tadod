package com.tadod.jobs.transformation

import com.tadod.config.Config
import com.tadod.jobs.base.BaseJob
import com.tadod.models.database.IcebergWriterConfig
import org.apache.log4j.{Level, LogManager, Logger}
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.functions.{col, to_date}

import java.time.LocalDate
import java.time.format.DateTimeFormatter

abstract class BaseMartJob(configPath: String, dateRun: String) extends BaseJob {
  protected val LOGGER: Logger = LogManager.getLogger(getClass.getName)
  Logger.getLogger("org").setLevel(Level.ERROR)

  loadConfig(configPath, getJobName)
  protected val icebergConfig: IcebergWriterConfig = loadIcebergConfig()

  protected def getJobName: String
  protected def createMart(sourceDf: DataFrame): DataFrame
  protected def writeToIceberg(targetDf: DataFrame): Unit

  def execute(): Unit = {
    try {
      LOGGER.info(s"Starting $getJobName transformation job")

      val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
      val currentDate = LocalDate.parse(dateRun, formatter)
      val previousDate = currentDate.minusDays(1).format(formatter)

      val sourceDf = spark.read
        .format("iceberg")
        .load(s"${icebergConfig.catalog}.${icebergConfig.schema}.${icebergConfig.table}")
        .filter(to_date(col("tpep_pickup_datetime")).equalTo(previousDate))

      val targetDf = createMart(sourceDf)

      LOGGER.info(s"=== $getJobName ===")
      targetDf.printSchema()
      targetDf.show(10, truncate = false)

      writeToIceberg(targetDf)

      println(s"$getJobName job finished successfully")
    } catch {
      case e: Exception =>
        LOGGER.error(s"$getJobName job failed: ${e.getMessage}", e)
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
        LOGGER.error("Failed o load Iceberg configuration", e)
        throw e
    }
  }
}
