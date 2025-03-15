package com.tadod.models.database

trait WriterConfig {
  def databaseType: String
  def schema: String
  def table: String
}
