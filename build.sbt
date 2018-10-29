import Dependencies._

lazy val commonSettings =
  inThisBuild(List(
    organization := "com.spikerlabs",
    scalaVersion := "2.12.7",
    version := "0.1.0-SNAPSHOT"
  ))

lazy val service = (project in file("service")).
  enablePlugins(CucumberPlugin).
  settings(
    name := "service",
    commonSettings,
    commonTestSettings
  )

lazy val api = (project in file("api")).
  enablePlugins(CucumberPlugin).
  settings(
    libraryDependencies ++= http4s,
    libraryDependencies ++= circe,
    libraryDependencies += logging,
    name := "api",
    commonSettings,
    commonTestSettings
  ).dependsOn(service)

lazy val commonTestSettings = Seq(
  libraryDependencies ++= cucumber,
  libraryDependencies += scalaTest,
  libraryDependencies += scalaCheck,
  CucumberPlugin.monochrome := false,
  CucumberPlugin.glue := "classpath:steps",
  CucumberPlugin.features := List("classpath:features")
)