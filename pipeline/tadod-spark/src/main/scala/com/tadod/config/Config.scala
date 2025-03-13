package com.tadod.config

object Config extends Serializable {
  var test: Map[String, String] = {
    Map()
  }

  var production: Map[String, String] = {
    Map()
  }

  var environment: String = sys.env.getOrElse("PROJECT_ENV", "production")

  def get(key: String): String = {
    if (environment == "test") {
      test(key)
    } else {
      production(key)
    }
  }
}
