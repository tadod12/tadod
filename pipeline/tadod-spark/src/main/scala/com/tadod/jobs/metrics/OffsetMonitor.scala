package com.tadod.jobs.metrics

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.prometheus.client.Gauge
import io.prometheus.client.exporter.HTTPServer

import org.apache.commons.io.IOUtils
import org.apache.kafka.clients.consumer.{ConsumerConfig, KafkaConsumer}
import org.apache.kafka.common.{PartitionInfo, TopicPartition}
import org.apache.kafka.common.serialization.ByteArrayDeserializer
import org.apache.log4j.Logger

import java.io.{File, FileFilter, FileInputStream}
import java.nio.charset.StandardCharsets
import java.util
import java.util.Properties
import scala.collection.JavaConverters._
import scala.collection.mutable.ArrayBuffer

class OffsetMonitor(port: Int, checkpointBasepath: String, bootstrapServers: String) {
  val LOGGER: Logger = Logger.getLogger(getClass.getName)

  def run(): Unit = {
    LOGGER.info("=== INFO ===")
    LOGGER.info(s"HTTP expose port: $port")
    LOGGER.info(s"Spark checkpoint base path: $checkpointBasepath")
    LOGGER.info(s"Kafka bootstrap servers: $bootstrapServers")

    new HTTPServer(port)
    val offsetGauge = Gauge.build()
      .name("kafka_topic_offset")
      .help("kafka_topic_offset")
      .labelNames("topic", "partition", "offset_type")
      .register()

    // Kafka config
    val config = new Properties
    config.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers)
    config.setProperty(ConsumerConfig.CLIENT_ID_CONFIG, "kafkaOffsetMonitoring")
    val consumer = new KafkaConsumer(config, new ByteArrayDeserializer, new ByteArrayDeserializer)

    while(true) {
      try {
        sparkOffset(checkpointBasepath, offsetGauge)
        kafkaOffset(consumer, offsetGauge)
      } catch {
        case e: Exception =>
          e.printStackTrace()
      }
      Thread.sleep(5000)
    }
  }

  private def kafkaOffset(consumer: KafkaConsumer[_, _], offsetGauge: Gauge): Unit = {
    val topics = consumer.listTopics().keySet().asScala
    topics.filter(_ == "yellow").foreach{ topic =>
      val partitions = listPartitionInfo(consumer, topic) match {
        case None =>
          LOGGER.info(s"Topic $topic does not exist")
          System.exit(1)
          Seq()
        case Some(p) if p.isEmpty =>
          LOGGER.info(s"Topic $topic has 0 partitions")
          System.exit(1)
          Seq()
        case Some(p) => p
      }

      val topicPartitions = partitions.sortBy(_.partition).flatMap { p =>
        if (p.leader == null) {
          LOGGER.info(s"Error: partition ${p.partition} does not have a leader. Skip getting offsets")
          None
        } else {
          Some(new TopicPartition(p.topic, p.partition))
        }
      }

      val endPartitionOffsets = consumer.endOffsets(topicPartitions.asJava).asScala

      endPartitionOffsets.toSeq.sortBy { case (tp, _) => tp.partition }.foreach {
        case (tp, offset) =>
          if (Option(offset).isDefined) {
            offsetGauge.labels(topic, tp.partition.toString, "end")
              .set(offset.doubleValue)
          }
          LOGGER.info(s"topic: $topic partition: ${tp.partition} end_offset: ${Option(offset).getOrElse("")}")
      }

      val beginPartitionOffsets = consumer.beginningOffsets(topicPartitions.asJava).asScala

      beginPartitionOffsets.toSeq.sortBy { case (tp, _) => tp.partition }.foreach {
        case (tp, offset) =>
          if (Option(offset).isDefined) {
            offsetGauge.labels(topic, tp.partition.toString, "begin")
              .set(offset.doubleValue)
          }
          LOGGER.info(s"topic: $topic partition: ${tp.partition} begin_offset: ${Option(offset).getOrElse("")}")
      }
    }
    LOGGER.info("=== KAFKA END ===")
  }

  private def sparkOffset(checkpointBasepath: String, offsetGauge: Gauge): Unit = {
    // (1) Get all folder under the checkpointBasepath
    val dirs = new File(checkpointBasepath).listFiles(new FileFilter() {
      override def accept(pathname: File): Boolean = pathname.isDirectory
    })

    val checkpoints = new ArrayBuffer[File]
    if (dirs == null) {
      LOGGER.info(s"No checkpoint folder found in $checkpointBasepath")
      return
    }
    for (p <- dirs) {
      val file = new File(p.getPath + "/offsets")
      if (file.exists) {
        checkpoints.append(file)
      }
    }

    // (2) For each checkpoint, get the offset file with the latest timestamp
    for (dir <- checkpoints) {
      var time: Long = Long.MinValue
      var fileOpt: Option[File] = None
      val files = dir.listFiles(new FileFilter() {
        override def accept(pathname: File): Boolean = pathname.isFile
      })

      for (f <- files) {
        if (f.lastModified > time && f.getName.matches("\\d+")) {
          time = f.lastModified
          fileOpt = Some(f)
        }
      }

      // (3) Use the offset file to get current offset
      if (fileOpt.isDefined) {
        val inputStream = new FileInputStream(fileOpt.get)
        val lines = IOUtils.readLines(inputStream, StandardCharsets.UTF_8).asScala

        for (line <- lines.slice(2, lines.length)) {
          val map: util.HashMap[String, util.HashMap[String, Double]] = new Gson()
            .fromJson(line,
              new TypeToken[util.HashMap[String, util.HashMap[String, Double]]] {}.getType)

          for (key <- map.keySet.asScala) {
            val partOffsetMap = map.get(key)
            for (partOffset <- partOffsetMap.asScala) {
              val partition = partOffset._1
              val offset = partOffset._2

              LOGGER.info(s"topic: $key appName: partition: $partition offset: $offset")
              offsetGauge.labels(key, partition, "current")
                .set(offset.doubleValue)
            }
          }
        }
      }
    }
    LOGGER.info("=== SPARK END ===")
  }

  private def listPartitionInfo(consumer: KafkaConsumer[_, _], topic: String): Option[Seq[PartitionInfo]] = {
    val partitionInfo = consumer.listTopics.asScala
      .filterKeys(_ == topic)
      .values
      .flatMap(_.asScala)
      .toBuffer

    if (partitionInfo.isEmpty) {
      None
    } else {
      Some(partitionInfo)
    }
  }
}
