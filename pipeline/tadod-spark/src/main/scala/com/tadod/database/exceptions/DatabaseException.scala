package com.tadod.database.exceptions

abstract class DatabaseException(
                                message: String,
                                cause: Throwable = null,
                                val errorCode: String = "DB_ERROR"
                                ) extends Exception(message, cause)

object DatabaseException {
  def unapply(e: DatabaseException): Option[(String, String)] =
    Some((e.getMessage, e.errorCode))
}