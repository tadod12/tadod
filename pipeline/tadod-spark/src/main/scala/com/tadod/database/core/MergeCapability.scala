package com.tadod.database.core

import org.apache.spark.sql.DataFrame

trait MergeCapability {
  def merge(
             sourceDF: DataFrame,
             targetTable: String,
             matchingColumns: Seq[String]
           ): Either[Throwable, Unit]
}
