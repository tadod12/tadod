ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.11.6"

lazy val root = (project in file("."))
  .settings(
    name := "spark",
    idePackagePrefix := Some("com.tadod")
  )
