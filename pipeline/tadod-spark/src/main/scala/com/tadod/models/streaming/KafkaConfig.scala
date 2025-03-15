package com.tadod.models.streaming

case class KafkaConfig(
                      bootstrapServers: String,
                      topics: Seq[String],
                      groupId: String,
                      startingOffsets: String = "latest",
                      checkpointLocation: String,
                      triggerInterval: String = "30 seconds",
                      maxOffsetsPerTrigger: Option[Long] = None
                      )
