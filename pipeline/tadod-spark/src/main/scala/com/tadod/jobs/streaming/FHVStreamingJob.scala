package com.tadod.jobs.streaming

import com.tadod.config.Config
import com.tadod.models.database.IcebergWriterConfig
import com.tadod.models.streaming.{IcebergOptimizeConfig, KafkaConfig}
import com.tadod.processors.{BaseStreamingProcessor, FHVStreamingProcessor}

class FHVStreamingJob(configPath: String) extends BaseStreamingJob(configPath) {

  override protected val processor = new FHVStreamingProcessor(
    spark, kafkaConfig, icebergConfig, optimizeConfig
  )

  override protected def getJobName: String = "FHVStreamingJob"

  override protected def loadKafkaConfig(): KafkaConfig = {
    try {
      val config = KafkaConfig(
        bootstrapServers = Config.get("kafka.bootstrap.servers"),
        topics = Config.get("kafka.topics").split(",").toSeq,
        groupId = Config.get("kafka.consumer.group.id"),
        checkpointLocation = Config.get("kafka.checkpoint.location"),
        triggerInterval = Config.get("kafka.trigger.interval"),
        maxOffsetsPerTrigger = Option(Config.get("kafka.max.offsets.per.trigger").toLong),
        startingOffsets = Config.get("kafka.starting.offsets")
      )

      LOGGER.info(
        s"""
           |Loaded Kafka config:
           |Bootstrap servers: ${config.bootstrapServers}
           |Topics: ${config.topics.mkString(",")}
           |Group ID: ${config.groupId}
           |Checkpoint location: ${config.checkpointLocation}
           |Trigger interval: ${config.triggerInterval}
           |""".stripMargin)

      config
    } catch {
      case e: Exception =>
        LOGGER.error("Failed to load Kafka configuration: ", e)
        throw e
    }
  }

  override protected def loadIcebergConfig(): IcebergWriterConfig = {
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

  override protected def loadOptimizeConfig(): IcebergOptimizeConfig = {
    try {
      val config = IcebergOptimizeConfig(
        targetFileSize = Config.get("iceberg.target.file.size.bytes").toLong,
        minInputFiles = Config.get("iceberg.min.input.files").toInt,
        commitIntervalMs = Config.get("iceberg.optimize.interval.ms").toLong,
        enableBinPack = Config.get("iceberg.enable.binpack").toBoolean,
        enableRewrite = Config.get("iceberg.enable.rewrite").toBoolean
      )

      LOGGER.info(
        s"""
           |Loaded Iceberg optimize config:
           |Target file size: ${config.targetFileSize}
           |Min input files: ${config.minInputFiles}
           |Commit interval: ${config.commitIntervalMs}
           |BinPack enable: ${config.enableBinPack}
           |Rewrite enable: ${config.enableRewrite}
           |""".stripMargin)

      config
    } catch {
      case e: Exception =>
        LOGGER.error("Failed to load Optimize configuration", e)
        throw e
    }
  }

  override def execute(): Unit = {
    try {
      LOGGER.info(s"Starting $getJobName streaming job")

      val kafkaDF = processor.readFromKafka()

      val processDF = processor.processStream(kafkaDF)

      processor.writeToIceberg(processDF)

      spark.streams.awaitAnyTermination()
    } catch {
      case e: Exception =>
        handleError(e, s"$getJobName failed")
    } finally {
      processor.stop()
    }
  }
}
