package com.tadod

import com.tadod.jobs.streaming.YellowStreamingJob

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
