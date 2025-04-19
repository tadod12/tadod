package com.tadod.processors

import com.tadod.models.database.IcebergWriterConfig
import com.tadod.models.schema.GreenSchema
import com.tadod.models.streaming.{IcebergOptimizeConfig, KafkaConfig}
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types._
import org.apache.spark.sql.{DataFrame, SparkSession}

class GreenStreamingProcessor(
                               override protected val spark: SparkSession,
                               override protected val kafkaConfig: KafkaConfig,
                               override protected val icebergConfig: IcebergWriterConfig,
                               override protected val optimizeConfig: IcebergOptimizeConfig
                             ) extends BaseStreamingProcessor {

  override def processStream(df: DataFrame): DataFrame = {
    LOGGER.info(s"Processing Green Streaming Data")

    try {
      val parsedDF = df.selectExpr("CAST(value AS STRING) as json")

      val jsonDF = parsedDF.withColumn("data", from_json(col("json"), GreenSchema.schema))

      val processedDF = jsonDF.select(
        col("data.vendor_id"),
        to_timestamp(col("data.lpep_pickup_datetime"), "yyyy-MM-dd HH:mm:ss").as("lpep_pickup_datetime"),
        to_timestamp(col("data.lpep_dropoff_datetime"), "yyyy-MM-dd HH:mm:ss").as("lpep_dropoff_datetime"),
        col("data.passenger_count").cast("int").as("passenger_count"),
        col("data.trip_distance"),
        col("data.rate_code_id").cast("int").as("rate_code_id"),
        col("data.store_and_fwd_flag"),
        col("data.pu_location_id"),
        col("data.do_location_id"),
        col("data.payment_type"),
        col("data.fare_amount"),
        col("data.extra"),
        col("data.mta_tax"),
        col("data.tip_amount"),
        col("data.tolls_amount"),
        col("data.improvement_surcharge"),
        col("data.total_amount"),
        col("data.congestion_surcharge"),
        col("data.trip_type").cast("int").as("trip_type"),
        // col("data.cbd_congestion_fee")
        col("data.ehail_fee")
      )

      processedDF
    } catch {
      case e: Exception =>
        LOGGER.error(s"Error processing stream: ${e.getMessage}")
        LOGGER.error("Stack traces: ", e)
        throw e
    }
  }
}
