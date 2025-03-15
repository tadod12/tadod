package com.tadod.models.database

case class IcebergWriterConfig(
                                catalog: String,
                                schema: String,
                                table: String,
                                partitionCols: Option[String] = None,
                                tempTable: Option[String] = None
                              ) extends WriterConfig {
  // Config Iceberg table information
  override def databaseType: String = "iceberg"
}
