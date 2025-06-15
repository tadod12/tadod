package com.tadod.jobs.transformation.yellow

import com.tadod.jobs.transformation.BaseMartJob
import org.apache.spark.sql.{DataFrame, SaveMode}
import org.apache.spark.sql.functions._

class YellowLocationExpand(configPath: String, dateRun: String) extends BaseMartJob(configPath, dateRun) {

  import spark.implicits._

  override protected def getJobName: String = "LocationExpand"

  override protected def createMart(sourceDf: DataFrame): DataFrame = {
    val anotherLookupDf = spark.read
      .format("csv")
      .option("header", value = true)
      .option("inferSchema", value = true)
      .load("file:///var/map/taxi_zone_lookup_coordinates.csv")
      .select(
        $"LocationID" as "location_id",
        $"latitude",
        $"longitude"
      )

    anotherLookupDf.persist()

    val oldLocationMart = spark.read
      .format("iceberg")
      .load("iceberg.clean.yellow")

    val pickUpdateDf = oldLocationMart
      .join(anotherLookupDf, anotherLookupDf("location_id") === $"pu_location_id", "left")
      .withColumnRenamed("latitude", "pu_location_latitude")
      .withColumnRenamed("longitude", "pu_location_longitude")
      .drop("location_id")

//      .select(
//        col("record_date"),
//        col("record_week"),
//        col("record_month"),
//        col("record_year"),
//        col("pu_location_id"),
//        col("pu_location_borough"),
//        col("pu_location_zone"),
//        col("pu_location_service_zone"),
//        col("latitude").as("pu_location_latitude"),
//        col("longitude").as("pu_location_longitude"),
//        col("do_location_id"),
//        col("do_location_borough"),
//        col("do_location_zone"),
//        col("do_location_service_zone"),
//        col("total_records")
//      )

    val targetDf = pickUpdateDf
      .join(anotherLookupDf, anotherLookupDf("location_id") === $"do_location_id", "left")
      .withColumnRenamed("latitude", "do_location_latitude")
      .withColumnRenamed("longitude", "do_location_longitude")
      .drop("location_id")
      .withColumn("pu_location_latitude", col("pu_location_latitude").cast("double"))
      .withColumn("pu_location_longitude", col("pu_location_longitude").cast("double"))
      .withColumn("do_location_latitude", col("do_location_latitude").cast("double"))
      .withColumn("do_location_longitude", col("do_location_longitude").cast("double"))

//      .select(
//        col("record_date"),
//        col("record_week"),
//        col("record_month"),
//        col("record_year"),
//        col("pu_location_id"),
//        col("pu_location_borough"),
//        col("pu_location_zone"),
//        col("pu_location_service_zone"),
//        col("pu_location_latitude").cast("double").as("pu_location_latitude"),
//        col("pu_location_longitude").cast("double").as("pu_location_longitude"),
//        col("do_location_id"),
//        col("do_location_borough"),
//        col("do_location_zone"),
//        col("do_location_service_zone"),
//        col("latitude").cast("double").as("do_location_latitude"),
//        col("longitude").cast("double").as("do_location_longitude"),
//        col("total_records")
//      )

    anotherLookupDf.unpersist()
    targetDf
  }

  override protected def writeToIceberg(targetDf: DataFrame): Unit = {
    targetDf.printSchema()

    targetDf.write
      .format("iceberg")
      .mode(SaveMode.Overwrite)
      .save("iceberg.clean.yellow_location_expand")
  }
}
