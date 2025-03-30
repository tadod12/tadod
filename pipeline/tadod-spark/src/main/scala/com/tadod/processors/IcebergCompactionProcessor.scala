package com.tadod.processors

import com.tadod.models.compaction.CompactionConfig
import org.apache.log4j.{Level, LogManager, Logger}
import org.apache.spark.sql.SparkSession

class IcebergCompactionProcessor(spark: SparkSession, config: CompactionConfig) {

  private val LOGGER = LogManager.getLogger(this.getClass)
  Logger.getLogger("org").setLevel(Level.ERROR)
  Logger.getLogger("Executor").setLevel(Level.ERROR)
  Logger.getLogger("CodecPool").setLevel(Level.ERROR)
  Logger.getLogger("TaskSetManager").setLevel(Level.ERROR)

  def process(): Unit = {
    val tablePath = s"${config.catalog}.${config.schema}.${config.table}"
    LOGGER.info(s"Starting compaction process for table: $tablePath")

    try {
      // Analyze file sizes before compaction
      analyzeFileSizes(tablePath)

      // Bin-pack wih optimal configuration
      runBinPackOptimization(tablePath)

      // Analyze file sizes after compaction
      analyzeFileSizes(tablePath)

      // Clean up
      cleanupOldData(tablePath)
    } catch {
      case e: Exception =>
        LOGGER.error(s"Compaction failed: ${e.getMessage}", e)
        throw e
    }
  }

  private def analyzeFileSizes(tablePath: String): Unit = {
    LOGGER.info("Analyzing file sizes...")
    val sampleSet = spark.sql(
      s"""SELECT *
         |FROM $tablePath.files""".stripMargin)
    sampleSet.printSchema()
    sampleSet.show(truncate = false)

    val stats = spark.sql(
      s"""SELECT
         |  count(*) as total_files,
         |  sum(file_size_in_bytes) as total_bytes,
         |  min(file_size_in_bytes) as min_file_bytes,
         |  max(file_size_in_bytes) as max_file_bytes,
         |  avg(file_size_in_bytes) as avg_file_bytes,
         |  count(CASE WHEN file_size_in_bytes < 104857600 THEN 1 END) as small_files /* < 100MB */
         |FROM $tablePath.files""".stripMargin)
    stats.show(false)
  }

  private def runBinPackOptimization(tablePath: String): Unit = {
    LOGGER.info("Starting bin-pack operation...")
    spark.sql(
        s"""CALL ${config.catalog}.system.rewrite_data_files(
           |table => '$tablePath',
           |strategy => 'binpack',
           |options => map(
           |  'min-input-files', '${config.minInputFiles}',
           |  'target-file-size-bytes', '${config.targetFileSize}',
           |  'max-file-group-size-bytes', '536870912',
           |  'partial-progress.enabled', 'true',
           |  'partial-progress.max-commits', '10'))""".stripMargin)
      .show()

    LOGGER.info("Bin-pack completed")
  }

  private def cleanupOldData(tablePath: String): Unit = {
    // Expire old snapshots
    LOGGER.info("Expiring old snapshots...")
    spark.sql(
        s"""CALL ${config.catalog}.system.expire_snapshots(
           |table => '$tablePath',
           |older_than => TIMESTAMP '$getExpirationTimestamp',
           |retain_last => 2,
           |max_concurrent_deletes => 4)""".stripMargin)
      .show()

    LOGGER.info("Removing obsolete files...")
    spark.sql(
        s"""CALL ${config.catalog}.system.remove_orphan_files(
           |table => '$tablePath',
           |older_than => TIMESTAMP '$getExpirationTimestampForOrphanFiles',
           |max_concurrent_deletes => 4)""".stripMargin)
      .show()
  }

  private def getExpirationTimestamp: String = {
    val cal = java.util.Calendar.getInstance()
    cal.add(java.util.Calendar.MINUTE, -config.expireSnapshotMinutes)
    new java.sql.Timestamp(cal.getTimeInMillis).toString
  }

  private def getExpirationTimestampForOrphanFiles: String = {
    val cal = java.util.Calendar.getInstance()
    cal.add(java.util.Calendar.HOUR, -24)
    new java.sql.Timestamp(cal.getTimeInMillis).toString
  }
}
