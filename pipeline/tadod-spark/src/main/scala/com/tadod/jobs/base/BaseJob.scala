package com.tadod.jobs.base

import com.tadod.config.Config
import com.tadod.utils.FileUtil.readConfig

class BaseJob extends SparkSessionWrapper {
  protected def loadConfig(configPath: String, jobName: String): Unit = {
    val prop = readConfig(configPath)

    Config.production += ("jobName" -> jobName)

    // Kafka Configurations
    Config.production += ("kafka.bootstrap.servers" -> prop.getProperty("kafka.bootstrap.servers"))
    Config.production += ("kafka.topics" -> prop.getProperty("kafka.topics"))
    Config.production += ("kafka.consumer.group.id" -> prop.getProperty("kafka.consumer.group.id"))
    Config.production += ("kafka.checkpoint.location" -> prop.getProperty("kafka.checkpoint.location"))
    Config.production += ("kafka.trigger.interval" -> prop.getProperty("kafka.trigger.interval", "30 seconds"))
    Config.production += ("kafka.max.offsets.per.trigger" -> prop.getProperty("kafka.max.offsets.per.trigger", "10000"))
    Config.production += ("kafka.starting.offsets" -> prop.getProperty("kafka.starting.offsets", "latest"))

    // Iceberg Configurations
    Option(prop.getProperty("iceberg.catalog")).foreach { catalog =>
      Config.production += ("iceberg.catalog" -> catalog)
    }
    Config.production += ("iceberg.schema" -> prop.getProperty("iceberg.schema"))
    Config.production += ("iceberg.table" -> prop.getProperty("iceberg.table"))
    Config.production += ("iceberg.partition.columns" -> prop.getProperty("iceberg.partition.columns"))

    // Iceberg File Optimization Configurations
    Config.production += ("iceberg.target.file.size.bytes" -> prop.getProperty("iceberg.target.file.size.bytes", "536870912"))
    Config.production += ("iceberg.min.input.files" -> prop.getProperty("iceberg.min.input.files", "10"))
    Config.production += ("iceberg.optimize.interval.ms" -> prop.getProperty("iceberg.optimize.interval.ms", "60000"))
    Config.production += ("iceberg.enable.binpack" -> prop.getProperty("iceberg.enable.binpack", "false"))
    Config.production += ("iceberg.enable.rewrite" -> prop.getProperty("iceberg.enable.rewrite", "false"))

    // Iceberg Compaction Configurations
    Config.production += ("iceberg.compaction.target.file.size.bytes" -> prop.getProperty("iceberg.compaction.target.file.size.bytes", "536870912"))
    Config.production += ("iceberg.compaction.min.input.files" -> prop.getProperty("iceberg.compaction.min.input.files", "5"))
    Config.production += ("iceberg.compaction.max.concurrent.groups" -> prop.getProperty("iceberg.compaction.max.concurrent.groups", "10"))
    Config.production += ("iceberg.compaction.commit.interval.ms" -> prop.getProperty("iceberg.compaction.commit.interval.ms", "60000"))
    Config.production += ("iceberg.compaction.partition.pattern" -> prop.getProperty("iceberg.compaction.partition.pattern"))
    Config.production += ("iceberg.compaction.enable.binpack" -> prop.getProperty("iceberg.compaction.enable.binpack", "true"))
    Config.production += ("iceberg.compaction.enable.sort" -> prop.getProperty("iceberg.compaction.enable.sort", "true"))
    Config.production += ("iceberg.compaction.sort.columns" -> prop.getProperty("iceberg.compaction.sort.columns"))
    Config.production += ("iceberg.compaction.delete.obsolete" -> prop.getProperty("iceberg.compaction.delete.obsolete", "true"))
    Config.production += ("iceberg.compaction.expire.snapshot.minutes" -> prop.getProperty("iceberg.compaction.expire.snapshot.minutes", "10080"))

    // Iceberg transform Configurations
    Config.production += ("iceberg.target.catalog" -> prop.getProperty("iceberg.target.catalog"))
    Config.production += ("iceberg.target.schema" -> prop.getProperty("iceberg.target.schema"))
    Config.production += ("iceberg.target.table" -> prop.getProperty("iceberg.target.table"))
  }
}
