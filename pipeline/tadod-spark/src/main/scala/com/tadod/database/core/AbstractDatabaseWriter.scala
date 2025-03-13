package com.tadod.database.core

import com.tadod.database.DatabaseWriter
import org.apache.spark.sql.DataFrame

abstract class AbstractDatabaseWriter extends DatabaseWriter {
  override def write(df: DataFrame): Either[Throwable, Unit] = {
    for {
      _ <- validate()
      _ <- beforeWrite()
      _ <- doWrite(df)
      _ <- afterWrite()
    } yield ()
  }

  protected def doWrite(df: DataFrame): Either[Throwable, Unit]
}
