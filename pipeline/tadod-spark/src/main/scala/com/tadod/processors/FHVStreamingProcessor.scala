package com.tadod.processors

import com.tadod.models.database.IcebergWriterConfig
import com.tadod.models.schema.FHVSchema
import com.tadod.models.streaming.{IcebergOptimizeConfig, KafkaConfig}
import org.apache.spark.sql.functions.{col, from_json, to_timestamp}
import org.apache.spark.sql.{DataFrame, SparkSession}

class FHVStreamingProcessor (
                              override protected val spark: SparkSession,
                              override protected val kafkaConfig: KafkaConfig,
                              override protected val icebergConfig: IcebergWriterConfig,
                              override protected val optimizeConfig: IcebergOptimizeConfig
                            ) extends BaseStreamingProcessor {

  override def processStream(df: DataFrame): DataFrame = {
    LOGGER.info(s"Processing FHV Streaming Data")

    try {
      val parsedDF = df.selectExpr("CAST(value AS STRING) as json")

      val jsonDF = parsedDF.withColumn("data", from_json(col("json"), FHVSchema.schema))

      val processedDF = jsonDF.select(
        col("data.dispatching_base_num"),
        to_timestamp(col("data.pickup_datetime"), "yyyy-MM-dd HH:mm:ss").as("pickup_datetime"),
        to_timestamp(col("data.dropoff_datetime"), "yyyy-MM-dd HH:mm:ss").as("dropoff_datetime"),
        col("data.pu_location_id"),
        col("data.do_location_id"),
        col("data.sr_flag"),
        col("data.affiliated_base_number")
      )

      processedDF
    } catch {
      case e: Exception =>
        LOGGER.error(s"Error processing stream: ${e.getMessage}")
        LOGGER.error("Stack trace: ", e)
        throw e
    }
  }
}
