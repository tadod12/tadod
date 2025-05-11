package com.tadod

import com.tadod.jobs.compaction.IcebergCompactionJob
import com.tadod.jobs.metrics.{KafkaOffsetMonitor, SparkOffsetMonitor}
import com.tadod.jobs.streaming.{FHVStreamingJob, GreenStreamingJob, YellowStreamingJob}
import com.tadod.jobs.transformation.{GreenCleaningJob, GreenMartVendorJob, MartLocationJob, MartPaymentJob, MartRateCodeJob, YellowCleaningJob, YellowMartVendorJob}

object App {
  def main(args: Array[String]): Unit = {
    showCommand()

    if (args.length >= 3) {
      val command = args(0)
      val configPath = args(1)
      val dateRun = args(2)

      println(s"command = $command")
      println(s"configPath = $configPath")
      println(s"dateRun = $dateRun")

      command match {
        case "YellowStream" => new YellowStreamingJob(
          configPath = configPath
        ).execute()

        case "GreenStream" => new GreenStreamingJob(
          configPath = configPath
        ).execute()

        case "FHVStream" => new FHVStreamingJob(
          configPath = configPath
        ).execute()

        case "IcebergCompaction" => new IcebergCompactionJob(
          configPath = configPath
        ).execute()

        case "YellowClean" => new YellowCleaningJob(
          configPath = configPath,
          dateRun = dateRun
        ).execute()

        case "GreenClean" => new GreenCleaningJob(
          configPath = configPath,
          dateRun = dateRun
        ).execute()

        case "YellowMartVendor" => new YellowMartVendorJob(
          configPath = configPath,
          dateRun = dateRun
        ).execute()

        case "GreenMartVendor" => new GreenMartVendorJob(
          configPath = configPath,
          dateRun = dateRun
        ).execute()

        case "MartRateCode" => new MartRateCodeJob(
          configPath = configPath,
          dateRun = dateRun
        ).execute()

        case "MartLocation" => new MartLocationJob(
          configPath = configPath,
          dateRun = dateRun
        ).execute()

        case "MartPayment" => new MartPaymentJob(
          configPath = configPath,
          dateRun = dateRun
        ).execute()

        case "SparkOffset" => new SparkOffsetMonitor(
          port = args(1).toInt,
          checkpointBasepath = args(2)
        ).run()

        case "KafkaOffset" => new KafkaOffsetMonitor(
          port = args(1).toInt,
          bootstrapServers = args(2)
        ).run()

        case _ => println(s"Unknown command: $command")
      }
    } else println("Not enough parameters!")
  }

  private def showCommand(): Unit = {
    val introduction =
      """
        |
        |  ████████╗ █████╗ ██████╗  ██████╗ ██████╗
        |  ╚══██╔══╝██╔══██╗██╔══██╗██╔═══██╗██╔══██╗
        |     ██║   ███████║██║  ██║██║   ██║██║  ██║
        |     ██║   ██╔══██║██║  ██║██║   ██║██║  ██║
        |     ██║   ██║  ██║██████╔╝╚██████╔╝██████╔╝
        |     ╚═╝   ╚═╝  ╚═╝╚═════╝  ╚═════╝ ╚═════╝   
        |
        |""".stripMargin

    println(introduction)
  }
}
