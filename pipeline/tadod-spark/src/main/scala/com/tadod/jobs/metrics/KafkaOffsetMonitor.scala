package com.tadod.jobs.metrics

import io.prometheus.client.Gauge
import io.prometheus.client.exporter.HTTPServer
import org.apache.kafka.clients.consumer.{ConsumerConfig, KafkaConsumer}
import org.apache.kafka.common.{PartitionInfo, TopicPartition}
import org.apache.kafka.common.serialization.ByteArrayDeserializer
import org.apache.log4j.Logger

import java.util.Properties
import scala.collection.JavaConverters._

class KafkaOffsetMonitor(port: Int, bootstrapServers: String) {
  val LOGGER: Logger = Logger.getLogger(getClass.getName)

  def run(): Unit = {
    LOGGER.info(s"httpPort: $port")
    LOGGER.info(s"kafkaBootstrapServers: $bootstrapServers")

    new HTTPServer(port)
    val offsetGauge = Gauge.build()
      .name("kafka_topic_offset")
      .help("kafka_topic_offset")
      .labelNames("topic", "partition", "offset_type")
      .register()

    val config = new Properties
    config.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers)
    config.setProperty(ConsumerConfig.CLIENT_ID_CONFIG, "kafkaMonitor")
    val consumer = new KafkaConsumer(config, new ByteArrayDeserializer, new ByteArrayDeserializer)

    while (true) {
      val topics = consumer.listTopics().keySet().asScala
      topics.filter(_ != "__consumer_offsets").foreach { topic =>
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
      LOGGER.info("=== END ===")
      Thread.sleep(5000)
    }

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
