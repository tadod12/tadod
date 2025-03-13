package com.tadod.database

import org.apache.spark.sql.DataFrame

trait DatabaseWriter {
  def write(df: DataFrame): Either[Throwable, Unit]
  def validate(): Either[Throwable, Boolean]
  protected def beforeWrite(): Either[Throwable, Unit] = Right(())
  protected def afterWrite(): Either[Throwable, Unit] = Right(())
}
