package com.tadod.database.exceptions

// Base exceptions - Connection & Operation
class DatabaseConnectionException(
                                   message: String,
                                   cause: Throwable = null,
                                   override val errorCode: String = "DB_CONN_ERROR"
                                 ) extends DatabaseException(message, cause, errorCode)

class DatabaseOperationException(
                                  message: String,
                                  cause: Throwable = null,
                                  override val errorCode: String = "DB_OP_ERROR"
                                ) extends DatabaseException(message, cause, errorCode)

// Concrete exceptions - Write, Read, Config, Validation
case class DatabaseWriteException(
                                   message: String,
                                   cause: Throwable = null,
                                   override val errorCode: String = "DB_WRITE_ERROR"
                                 ) extends DatabaseOperationException(message, cause, errorCode)

case class DatabaseReadException(
                                  message: String,
                                  cause: Throwable = null,
                                  override val errorCode: String = "DB_READ_ERROR"
                                ) extends DatabaseOperationException(message, cause, errorCode)

case class DatabaseConfigException(
                                    message: String,
                                    cause: Throwable = null,
                                    override val errorCode: String = "DB_CONFIG_ERROR"
                                  ) extends DatabaseException(message, cause, errorCode)

case class DatabaseValidationException(
                                        message: String,
                                        cause: Throwable = null,
                                        override val errorCode: String = "DB_VALIDATION_ERROR"
                                      ) extends DatabaseException(message, cause, errorCode)