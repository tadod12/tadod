package com.tadod.database.core

import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.types.StructField

trait ColumnManagement {
  // Add and find missing columns
  def addMissingColumns(sourceDF: DataFrame, targetColumns: Set[String]): Either[Throwable, Unit]

  protected def getMissingColumns(sourceDF: DataFrame, targetColumns: Set[String]): Seq[StructField] = {
    val targetColumnsLower = targetColumns.map(_.toLowerCase)
    sourceDF.schema.fields.filterNot(field =>
      targetColumnsLower.contains(field.name.toLowerCase)
    )
  }
}
