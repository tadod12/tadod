package com.tadod.processors

import com.tadod.models.database.IcebergWriterConfig
import com.tadod.models.streaming.{IcebergOptimizeConfig, KafkaConfig}
import org.apache.log4j.{LogManager, Logger}
import org.apache.spark.sql.streaming.Trigger
import org.apache.spark.sql.{DataFrame, SparkSession}

trait BaseStreamingProcessor {
  protected val spark: SparkSession
  protected val kafkaConfig: KafkaConfig
  protected val icebergConfig: IcebergWriterConfig
  protected val optimizeConfig: IcebergOptimizeConfig
  protected val LOGGER: Logger = LogManager.getLogger(this.getClass)

  // Default implementation for Kafka reading
  def readFromKafka(): DataFrame = {
    LOGGER.info(
      s"""
         |Starting to read from Kafka:
         |Bootstrap servers: ${kafkaConfig.bootstrapServers}
         |Topics: ${kafkaConfig.topics.mkString(",")}
         |Group ID: ${kafkaConfig.groupId}
         |Starting offsets: ${kafkaConfig.startingOffsets}
         |""".stripMargin)

    spark.readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", kafkaConfig.bootstrapServers)
      .option("subscribe", kafkaConfig.topics.mkString(","))
      .option("startingOffsets", kafkaConfig.startingOffsets)
      .option("kafka.group.id", kafkaConfig.groupId)
      .option("failOnDataLoss", "false")
      .option("kafka.security.protocol", "PLAINTEXT")
      .load()
  }

  // Abstract method that each processor must implement
  def processStream(df: DataFrame): DataFrame

  // Default implementation for Iceberg writing
  def writeToIceberg(df: DataFrame): Unit = {
    LOGGER.info(s"Writing to Iceberg table: ${icebergConfig.table}")

    try {
      // Schema comparison
      val tablePath = s"${icebergConfig.catalog}.${icebergConfig.schema}.${icebergConfig.table}"

      LOGGER.info("=== ICEBERG TABLE SCHEMA ===")
      val tableSchema = spark.read.format("iceberg").load(tablePath).schema
      LOGGER.info(tableSchema.treeString)

      LOGGER.info("=== DATAFRAME SCHEMA ===")
      LOGGER.info(df.schema.treeString)

      if (!tableSchema.equals(df.schema)) {
        LOGGER.error("Schema mismatch between DataFrame and Iceberg table\n" +
          "Table fields: " + tableSchema.fieldNames.mkString(", ") + "\n" +
          "DataFrame fields: " + df.schema.fieldNames.mkString(", "))
        throw new Exception("Schema mismatch")
      }

      // Write stream with monitoring
      df.writeStream
        .format("iceberg")
        .outputMode("append")
        .option("path", tablePath)
        .option("checkpointLocation", s"${kafkaConfig.checkpointLocation}/${icebergConfig.table}")
        .trigger(Trigger.ProcessingTime(kafkaConfig.triggerInterval))
        .foreachBatch { (batchDF: DataFrame, batchId: Long) =>
          LOGGER.info(s"Processing batch $batchId with ${batchDF.count()} records")
          LOGGER.info("Sample records:")
          batchDF.show(5, truncate = false)

          // Write to Iceberg
          batchDF.write
            .format("iceberg")
            .mode("append")
            .save(tablePath)
        }
        .start()

      LOGGER.info("Streaming query started successfully")
    } catch {
      case e: Exception =>
        LOGGER.error(s"Error writing to Iceberg: ${e.getMessage}")
        LOGGER.error("Stack trace: ", e)
        throw e
    }
  }

  def stop(): Unit = {
    try {
      LOGGER.info("Stopping processor")
    } catch {
      case e: Exception =>
        LOGGER.error("Error stopping processor ", e)
    }
  }
}
