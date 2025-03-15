package com.tadod.models.streaming

case class IcebergOptimizeConfig(
                                  targetFileSize: Long = 512 * 1024 * 1024, // Default 512MB
                                  minInputFiles: Int = 10,
                                  commitIntervalMs: Long = 60000,
                                  enableBinPack: Boolean = true,
                                  enableRewrite: Boolean = true
                                )
