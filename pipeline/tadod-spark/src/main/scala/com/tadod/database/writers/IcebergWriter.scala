package com.tadod.database.writers

import com.tadod.database.core.{AbstractDatabaseWriter, MergeCapability}
import com.tadod.database.exceptions.{DatabaseConfigException, DatabaseWriteException}
import com.tadod.models.database.IcebergWriterConfig

import org.apache.log4j.LogManager
import org.apache.spark.sql.{DataFrame, SaveMode, SparkSession}
import org.apache.spark.sql.catalyst.analysis.{NoSuchDatabaseException, NoSuchTableException}
import org.apache.spark.sql.functions._

import java.time.LocalDateTime
import scala.util.Try

class IcebergWriter(spark: SparkSession, config: IcebergWriterConfig)
  extends AbstractDatabaseWriter with MergeCapability {
  // Logger
  private val LOGGER = LogManager.getLogger(getClass.getName)

  private val columnManager = new IcebergColumnManager(spark, config)

  override def validate(): Either[Throwable, Boolean] = {
    try {
      // Required configs
      require(config != null, "IcebergWriterConfig cannot be null")
      require(config.catalog != null, "Iceberg catalog cannot be null")
      require(config.schema != null, "Iceberg schema cannot be null")
      require(config.table != null, "Iceberg table cannot be null")

      LOGGER.info(
        s"""
           |=== VALIDATING ICEBERG CONFIGURATION ===
           |Catalog: ${config.catalog}
           |Schema: ${config.schema}
           |Table: ${config.table}
           |Partition Columns: ${config.partitionCols.getOrElse("None")}
           |========================================
           |""".stripMargin)

      Right(true)
    } catch {
      case e: DatabaseConfigException =>
        LOGGER.error(s"Iceberg validation failed: ${e.getMessage}", e)
        Left(e)
    }
  }

  override protected def doWrite(df: DataFrame): Either[Throwable, Unit] = {
    Try {
      createDatabaseIfNotExists(config.schema)
      createTableIfNotExists(df, config.schema, config.table, config.partitionCols)

      val targetColumns = getTargetColumns(config.table)
      val sourceColumns = df.columns.map(_.toLowerCase).toSet

      val missingColumns = sourceColumns.diff(targetColumns)
      if (missingColumns.nonEmpty) {
        LOGGER.info(s"Found new columns to add: ${missingColumns.mkString(", ")}")
        columnManager.addMissingColumns(df, targetColumns) match {
          case Left(error) => throw error
          case Right(_) => LOGGER.info("Successfully added new columns")
        }
      }

      // Expire old snapshots
      expireSnapshot(
        table = s"${config.schema}.${config.table}",
        catalog = config.catalog,
        numDateExpire = 1, // retain 1 day before
        retainLast = 2 // retain 2 most recent snapshots
      )
    }.toEither.left.map { e =>
      DatabaseWriteException(s"Failed to write to Iceberg table: ${e.getMessage}", e)
    }
  }

  override def merge(sourceDF: DataFrame, targetTable: String, matchingColumns: Seq[String]): Either[Throwable, Unit] = {
    for {
      _ <- columnManager.addMissingColumns(sourceDF, getTargetColumns(targetTable))
      result <- performMerge(sourceDF, targetTable, matchingColumns)
    } yield result
  }

  private def createDatabaseIfNotExists(schemaTarget: String): Unit = {
    try {
      spark.sql(s"USE ${config.catalog}.$schemaTarget")
      LOGGER.info(s"Schema $schemaTarget already exists")
    } catch {
      case _: NoSuchDatabaseException =>
        LOGGER.info(s"Schema $schemaTarget does not exist. Creating schema...")
        spark.sql(s"CREATE DATABASE IF NOT EXISTS ${config.catalog}.$schemaTarget")
        LOGGER.info(s"Schema $schemaTarget create successfully")
    }
  }

  def createTableIfNotExists(df: DataFrame, schemaTarget: String, tableTarget: String, colPartition: Option[String]): Unit = {
    try {
      spark.read.format("iceberg").load(s"${config.catalog}.$schemaTarget.$tableTarget")
      LOGGER.info(s"Table ${config.catalog}.$schemaTarget.$tableTarget already exists")
    } catch {
      case _: NoSuchTableException =>
        LOGGER.info(s"Table ${config.catalog}.$schemaTarget.$tableTarget does not exist. Creating table with schema only...")

        val emptyDf = df.limit(0)
        var writer = emptyDf.writeTo(s"${config.catalog}.$schemaTarget.$tableTarget").using("iceberg")
        colPartition match {
          case Some(partitionColumn) if partitionColumn != null && partitionColumn.nonEmpty =>
            writer = writer.partitionedBy(days(col(partitionColumn)))
          case _ =>
            LOGGER.info("No valid partition column specified, creating table without partitioning")
        }

        writer.tableProperty("format", "parquet").createOrReplace()
        emptyDf.writeTo(s"${config.catalog}.$schemaTarget.$tableTarget")
          .using("iceberg")
          .tableProperty("format", "parquet")
          .createOrReplace()

        LOGGER.info(s"Table ${config.catalog}.$schemaTarget.$tableTarget created successfully with partitioning")
    }
  }

  private def expireSnapshot(table: String, catalog: String, numDateExpire: Long = 2, retainLast: Long = 1): Unit = {
    val lastDate = LocalDateTime.now()
      .minusDays(numDateExpire)
      .withHour(23)
      .withMinute(59)
      .withSecond(59)
      .withNano(0)

    val tablePath = s"$catalog.$table".toLowerCase

    LOGGER.info(
      s"""
         |=== EXPIRING ICEBERG SNAPSHOTS ===
         |Table: $tablePath
         |Expire Before: $lastDate
         |Retain Last: $retainLast snapshots
         |=================================
         |""".stripMargin)

    try {
      spark.sql(
        s"""
           |CALL $catalog.system.expire_snapshots(
           |  table => '$tablePath',
           |  older_than => TIMESTAMP '$lastDate',
           |  retain_last => $retainLast
           |)""".stripMargin)

      LOGGER.info(s"Successfully expired snapshots for $tablePath")
    } catch {
      case e: Exception =>
        val errorMsg =
          s"""
             |=== FAILED TO EXPIRE SNAPSHOTS ===
             |Table: $tablePath
             |Error: ${e.getMessage}
             |Stack Trace:
             |${e.getStackTrace.mkString("\n")}
             |==================================
             |""".stripMargin

        LOGGER.error(errorMsg)
        throw e
    }
  }

  private def getTargetColumns(tableName: String): Set[String] = {
    val targetTablePath = s"${config.catalog}.${config.schema}.${tableName.toLowerCase}".toLowerCase()
    val targetDf = spark.table(targetTablePath)

    targetDf.printSchema()
    targetDf.schema.fields.map(_.name.toLowerCase).toSet
  }

  private def performMerge(sourceDF: DataFrame, targetTable: String, matchingColumns: Seq[String]): Either[Throwable, Unit] = Try {
    // Validate target table name
    if (targetTable == null || targetTable.isEmpty) {
      throw new IllegalArgumentException("Target table name cannot be null or empty")
    }

    val finalTargetTable = if (targetTable == "None") config.table.toLowerCase else targetTable.toLowerCase
    val targetTablePath = s"${config.catalog}.${config.schema}.$finalTargetTable".toLowerCase

    // Write to temp table
    val tempTablePath = s"${targetTablePath}_temp".toLowerCase
    sourceDF
      .repartition(3)
      .write
      .format("iceberg")
      .mode(SaveMode.Overwrite)
      .saveAsTable(tempTablePath)

    val mergeCondition = matchingColumns.map(col =>
      s"source.${col.toLowerCase} = target.${col.toLowerCase}"
    ).mkString(" AND ")

    val updateSetClause = sourceDF.columns
      .map(_.toLowerCase)
      .filterNot(matchingColumns.map(_.toLowerCase).contains)
      .map(col => s"target.$col = source.$col")
      .mkString(", ")

    val insertColumns = sourceDF.columns.map(_.toLowerCase).mkString(", ")
    val insertValues = sourceDF.columns.map(col => s"source.${col.toLowerCase}").mkString(", ")

    println(s"Merging data from $tempTablePath into $targetTablePath")

    spark.sql(
      s"""
         |MERGE INTO $targetTablePath target
         |USING $tempTablePath source
         |ON $mergeCondition
         |WHEN MATCHED THEN UPDATE SET $updateSetClause
         |WHEN NOT MATCHED THEN INSERT ($insertColumns) VALUES ($insertValues)
         |""".stripMargin)

    dropTable(tempTablePath)
    ()
  }.toEither.left.map { e =>
    DatabaseWriteException(s"Failed to merge into Iceberg table: ${e.getMessage}", e)
  }

  private def dropTable(tablePath: String): Unit = {
    LOGGER.info(
      s"""
         |=== DROPPING ICEBERG TABLE ===
         |Table: $tablePath
         |==============================
         |""".stripMargin)

    try {
      spark.sql(s"DROP TABLE IF EXISTS $tablePath PURGE")
      LOGGER.info(s"Successfully dropped table: $tablePath")
    } catch {
      case e: Exception =>
        val errorMsg =
          s"""
             |=== FAILED TO DROP ICEBERG TABLE ===
             |Table: $tablePath
             |Error: ${e.getMessage}
             |Stack trace:
             |${e.getStackTrace.mkString("\n")}
             |====================================
             |""".stripMargin

        LOGGER.error(errorMsg)
        throw e
    }
  }
}
