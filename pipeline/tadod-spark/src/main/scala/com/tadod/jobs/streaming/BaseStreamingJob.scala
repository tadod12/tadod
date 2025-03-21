package com.tadod.jobs.streaming

import com.tadod.jobs.base.BaseJob
import com.tadod.models.database.IcebergWriterConfig
import com.tadod.models.streaming.{IcebergOptimizeConfig, KafkaConfig}
import com.tadod.processors.BaseStreamingProcessor
import org.apache.log4j.{Level, LogManager, Logger}

abstract class BaseStreamingJob(configPath: String) extends BaseJob {
  protected val LOGGER: Logger = LogManager.getLogger(getClass.getName)
  Logger.getLogger("org").setLevel(Level.ERROR)

  // Load configurations
  loadConfig(configPath, getJobName)

  protected val kafkaConfig: KafkaConfig = loadKafkaConfig()
  protected val icebergConfig: IcebergWriterConfig = loadIcebergConfig()
  protected val optimizeConfig: IcebergOptimizeConfig = loadOptimizeConfig()

  protected val processor: BaseStreamingProcessor

  protected def getJobName: String
  protected def loadKafkaConfig(): KafkaConfig
  protected def loadIcebergConfig(): IcebergWriterConfig
  protected def loadOptimizeConfig(): IcebergOptimizeConfig

  def execute(): Unit = {
    try {
      LOGGER.info(s"Starting $getJobName streaming job")

      // Read from Kafka
      val kafkaDF = processor.readFromKafka()

      // Process stream
      val processedDF = processor.processStream(kafkaDF)

      // Write to Iceberg
      processor.writeToIceberg(processedDF)

      // Wait for termination
      spark.streams.awaitAnyTermination()
    } catch {
      case e: Exception =>
        handleError(e, "BaseStreamingJob failed")
    } finally {
      processor.stop()
    }
  }

  protected def handleError(e: Exception, message: String): Unit = {
    LOGGER.error(s"$message: ${e.getMessage}", e)
  }
}
