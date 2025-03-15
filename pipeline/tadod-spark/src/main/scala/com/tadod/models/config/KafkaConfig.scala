package com.tadod.models.config

case class KafkaConfig(
                        jobId: Int,
                        jobName: String,
                        topics: String,
                        groupId: String,
                        brokers: String,
                        schemaName: String,
                        tableName: String,
                        databaseType: String,
                        targetServer: String,
                        batchInterval: String = "1 minutes",
                        checkpointLocation: String
                      )
