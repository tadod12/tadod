package com.tadod.jobs.compaction

import com.tadod.config.Config
import com.tadod.jobs.base.BaseJob
import com.tadod.models.compaction.CompactionConfig
import com.tadod.processors.IcebergCompactionProcessor
import org.apache.log4j.{Level, Logger}

class IcebergCompactionJob(configPath: String) extends BaseJob {
  //  private val LOGGER = LogManager.getLogger(getClass.getName)
  Logger.getLogger("org").setLevel(Level.ERROR)

  // Load Configuration
  loadConfig(configPath, "Iceberg File Compaction")
  private val compactionConfig = CompactionConfig(
    catalog = Config.get("iceberg.catalog"),
    schema = Config.get("iceberg.schema"),
    table = Config.get("iceberg.table"),
    targetFileSize = Config.get("iceberg.compaction.target.file.size.bytes").toLong,
    minInputFiles = Config.get("iceberg.compaction.min.input.files").toInt,
    maxConcurrentFileGroups = Config.get("iceberg.compaction.max.concurrent.groups").toInt,
    commitIntervalMs = Config.get("iceberg.compaction.commit.interval.ms").toLong,
    enableBinPack = Config.get("iceberg.compaction.enable.binpack").toBoolean,
    enableSort = Config.get("iceberg.compaction.enable.sort").toBoolean,
    deleteObsoleteFiles = Config.get("iceberg.compaction.delete.obsolete").toBoolean,
    expireSnapshotMinutes = Config.get("iceberg.compaction.expire.snapshot.minutes").toInt
  )

  private val processor = new IcebergCompactionProcessor(spark, compactionConfig)

  def execute(): Unit = {
    processor.process()
  }
}
