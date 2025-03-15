package com.tadod.processors

import com.tadod.models.database.IcebergWriterConfig
import com.tadod.models.schema.YellowSchema
import com.tadod.models.streaming.{IcebergOptimizeConfig, KafkaConfig}
import org.apache.spark.sql.functions.{col, from_json}
import org.apache.spark.sql.{DataFrame, SparkSession}

class YellowStreamingProcessor(
                                override protected val spark: SparkSession,
                                override protected val kafkaConfig: KafkaConfig,
                                override protected val icebergConfig: IcebergWriterConfig,
                                override protected val optimizeConfig: IcebergOptimizeConfig
                              ) extends BaseStreamingProcessor {

  override def processStream(df: DataFrame): DataFrame = {
    LOGGER.info(s"Processing Yellow Streaming Data")

    try {
      val parsedDF = df.selectExpr("CAST(value AS STRING) as json")

      val jsonDF = parsedDF.withColumn("data", from_json(col("json"), YellowSchema.schema))

      val processedDF = jsonDF.select(
        col("data.payload.after.id").as("id"),
        col("data.payload.after.vendor_id").as("vendor_id"),
        col("data.payload.after.tpep_pickup_datetime").as("tpep_pickup_datetime"),
        col("data.payload.after.tpep_dropoff_datetime").as("tpep_dropoff_datetime"),
        col("data.payload.after.passenger_count").as("passenger_count"),
        col("data.payload.after.trip_distance").as("trip_distance"),
        col("data.payload.after.pu_location_id").as("pu_location_id"),
        col("data.payload.after.do_location_id").as("do_location_id"),
        col("data.payload.after.rate_code_id").as("rate_code_id"),
        col("data.payload.after.store_and_fwd_flag").as("store_and_fwd_flag"),
        col("data.payload.after.payment_type").as("payment_type"),
        col("data.payload.after.fare_amount").as("fare_amount"),
        col("data.payload.after.extra").as("extra"),
        col("data.payload.after.mta_tax").as("mta_tax"),
        col("data.payload.after.improvement_surcharge").as("improvement_surcharge"),
        col("data.payload.after.tip_amount").as("tip_amount"),
        col("data.payload.after.tolls_amount").as("tolls_amount"),
        col("data.payload.after.total_amount").as("total_amount"),
        col("data.payload.after.congestion_surcharge").as("congestion_surcharge"),
        col("data.payload.after.airport_fee").as("airport_fee")
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
