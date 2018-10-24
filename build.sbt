import Dependencies._

lazy val root = (project in file(".")).
  enablePlugins(CucumberPlugin).
  settings(
    inThisBuild(List(
      organization := "com.spikerlabs",
      scalaVersion := "2.12.7",
      version      := "0.1.0-SNAPSHOT"
    )),
    name := "merchant-offer-service"
  )
  // test dependencies
  .settings(
    libraryDependencies ++= cucumber,
    libraryDependencies += scalaTest,
    libraryDependencies += scalaCheck,
    CucumberPlugin.monochrome := false,
    CucumberPlugin.glue := "classpath:steps",
    CucumberPlugin.features := List("classpath:features")
  )