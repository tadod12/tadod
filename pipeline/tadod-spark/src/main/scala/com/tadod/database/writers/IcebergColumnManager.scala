package com.tadod.database.writers

import com.tadod.database.core.ColumnManagement
import com.tadod.database.exceptions.DatabaseWriteException
import com.tadod.models.database.IcebergWriterConfig
import org.apache.log4j.LogManager
import org.apache.spark.sql.types._
import org.apache.spark.sql.{DataFrame, SparkSession}

import scala.util.Try

class IcebergColumnManager(spark: SparkSession, config: IcebergWriterConfig) extends ColumnManagement {
  private val LOGGER = LogManager.getLogger(getClass.getName)

  override def addMissingColumns(sourceDF: DataFrame, targetColumns: Set[String]): Either[Throwable, Unit] = {
    Try {
      val sourceColumns = sourceDF.columns.map(_.toLowerCase).toSet
      val missingColumns = sourceColumns.diff(targetColumns)

      if (missingColumns.nonEmpty) {
        LOGGER.info(s"Adding missing columns: ${missingColumns.mkString(", ")}")

        missingColumns.foreach { columnName =>
          val field = sourceDF.schema.fields.find(_.name.toLowerCase == columnName.toLowerCase).get
          val dataType = getIcebergDataType(field.dataType)

          val alterSql =
            s"""
               |ALTER TABLE ${config.catalog}.${config.schema}.${config.table}
               |ADD COLUMN ${columnName.toLowerCase} $dataType
               |""".stripMargin

          LOGGER.info(s"Executing: $alterSql")
          spark.sql(alterSql)
        }

        // Verify added columns
        val updatedColumns = getTargetColumns(config.table)
        val stillMissing = missingColumns.diff(updatedColumns)
        if (stillMissing.nonEmpty) {
          throw new DatabaseWriteException(s"Failed to add columns: ${stillMissing.mkString(", ")}")
        }
      }
    }.toEither
  }

  private def getIcebergDataType(sparkType: DataType): String = {
    // Get database datatype from spark datatype
    sparkType match {
      case StringType => "string"
      case IntegerType => "int"
      case LongType => "bigint"
      case DoubleType => "double"
      case DecimalType() => "decimal(38,18)"
      case TimestampType => "timestamp"
      case DateType => "date"
      case BooleanType => "boolean"
      case _ => "string"
    }
  }

  def getTargetColumns(tableName: String): Set[String] = {
    // Get columns in database table
    val tablePath = s"${config.catalog}.${config.schema}.${tableName.toLowerCase}"
    try {
      spark.table(tablePath)
        .schema
        .fields
        .map(_.name.toLowerCase)
        .toSet
    } catch {
      case e: Exception =>
        LOGGER.error(s"Failed to get columns for table $tableName ", e)
        Set.empty[String]
    }
  }
}
