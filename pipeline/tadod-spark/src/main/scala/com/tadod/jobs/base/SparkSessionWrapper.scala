package com.tadod.jobs.base

import com.tadod.config.Config
import org.apache.spark.SparkConf
//import org.apache.spark.sql.{DataFrame, Dataset, SaveMode}
import org.apache.spark.sql.SparkSession
import org.apache.log4j.{Level, Logger}
//import org.apache.log4j.LogManager

trait SparkSessionWrapper extends Serializable {
  // Turn off unnecessary logs
  Logger.getLogger("org").setLevel(Level.OFF)
  Logger.getLogger("akka").setLevel(Level.OFF)
  Logger.getLogger("com.amazonaws").setLevel(Level.OFF)
  Logger.getLogger("io.netty").setLevel(Level.OFF)
  Logger.getLogger("org.apache.kafka.clients.consumer.internals.SubscriptionState").setLevel(Level.OFF)
  Logger.getLogger("org.apache.kafka.clients.consumer.KafkaConsumer").setLevel(Level.OFF)
  Logger.getLogger("Executor").setLevel(Level.OFF)
  Logger.getLogger("MicroBatchExecution").setLevel(Level.OFF)

  //  private val LOGGER: Logger = LogManager.getLogger(getClass.getName)

  // SparkSession initialization
  lazy val spark: SparkSession = {
    val sparkConf = new SparkConf()
      .setAppName(Config.get("jobName"))
      .set("spark.ui.showConsoleProgress", "true")
      .set("spark.sql.sources.partitionOverwriteMode", "dynamic")

    SparkSession
      .builder()
      .config(sparkConf)
      .getOrCreate()
  }

  //  def stringToMap(input: String): Map[String, String] = {
  //    val cleanedInput = input.stripPrefix("Map(").stripSuffix(")")
  //    val pairs = cleanedInput.split(",").map { pair =>
  //      val keyValue = pair.split("->").map(_.trim.replace("\"", ""))
  //      keyValue(0) -> keyValue(1)
  //    }
  //    pairs.toMap
  //  }

  //  def parseMapString(input: String): Map[String, String] = {
  //    // Remove "Map(...)"
  //    val cleanedInput = input.stripPrefix("Map(").stripSuffix(")")
  //
  //    // Split key-value pairs
  //    val keyValuePairs = cleanedInput.split(", ").map(_.trim)
  //
  //    // Construct map
  //    keyValuePairs.map { pair =>
  //      val keyValue = pair.split("->").map(_.trim)
  //      if (keyValue.length == 2) {
  //        val key = keyValue(0).replace("\"", "")
  //        val value = keyValue(1).replace("\"", "")
  //        key -> value
  //      } else {
  //        throw new IllegalArgumentException(s"Invalid map string: $input")
  //      }
  //    }.toMap
  //  }

  //  lazy val repartitionNumber: Int => Int = (repartitionFactor: Int) => {
  //    try {
  //      val executorInstances = spark.conf.get("spark.executor.instances").toInt
  //      val executorCores = spark.conf.get("spark.executor.cores").toInt
  //      executorInstances * executorCores * repartitionFactor
  //    } catch {
  //      case _: Exception => 200
  //    }
  //  }

  //  lazy val getNumberExecutorCoresFromAllExecutors: Int = {
  //    try {
  //      val executorInstances = spark.conf.get("spark.executor.instances").toInt
  //      val executorCores = spark.conf.get("spark.executor.cores").toInt
  //      executorInstances * executorCores
  //    } catch {
  //      case _: Exception => 200
  //    }
  //  }

  //  def repartitionByRecords(ds: Dataset[String], recordsPerPartition: Int): Dataset[String] = {
  //    val totalRecords = ds.count()
  //    // Number of partitions needed
  //    val numPartitions = Math.ceil(totalRecords.toDouble / recordsPerPartition).toInt
  //    ds.repartition(numPartitions)
  //  }
}
