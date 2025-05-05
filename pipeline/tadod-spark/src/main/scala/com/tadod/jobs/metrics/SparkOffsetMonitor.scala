package com.tadod.jobs.metrics

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.prometheus.client.Gauge
import io.prometheus.client.exporter.HTTPServer
import org.apache.commons.io.IOUtils
import org.apache.log4j.Logger

import java.io.{File, FileFilter, FileInputStream}
import java.nio.charset.StandardCharsets
import java.util
import scala.collection.JavaConverters._
import scala.collection.mutable.ArrayBuffer

class SparkOffsetMonitor(port: Int, checkpointBasepath: String) {
  val LOGGER: Logger = Logger.getLogger(getClass.getName)

  def run(): Unit = {
    LOGGER.info(s"httpPort: $port")
    LOGGER.info(s"checkpointBasepath: $checkpointBasepath")

    new HTTPServer(port)
    val offsetGauge = Gauge.build()
      .name("kafka_topic_offset")
      .help("kafka_topic_offset")
      .labelNames("topic", "partition", "offset_type")
      .register()

    while(true) {
      try {
        run(checkpointBasepath, offsetGauge)
      } catch {
        case e: Exception =>
          e.printStackTrace()
      }
      Thread.sleep(5000)
    }
  }

  private def run(checkpointBasepath: String, offsetGauge: Gauge): Unit = {
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
        override def accept(file: File): Boolean = file.isFile
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
    LOGGER.info("=== END ===")
  }
}
