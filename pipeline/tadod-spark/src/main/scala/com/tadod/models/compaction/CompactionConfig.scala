package com.tadod.models.compaction

case class CompactionConfig(
                             catalog: String,
                             schema: String,
                             table: String,
                             targetFileSize: Long = 512 * 1024 * 1024, // default 512MB
                             minInputFiles: Int = 5,
                             maxConcurrentFileGroups: Int = 10,
                             commitIntervalMs: Long = 60000,
                             partitionPattern: Option[String] = None,
                             enableBinPack: Boolean = true,
                             enableSort: Boolean = true,
                             sortColumns: Option[Seq[String]] = None,
                             deleteObsoleteFiles: Boolean = true,
                             expireSnapshotMinutes: Int = 7 * 24 * 60
                           )
