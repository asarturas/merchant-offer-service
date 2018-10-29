import Dependencies._

lazy val service = (project in file("service")).
  enablePlugins(CucumberPlugin).
  settings(
    libraryDependencies ++= http4s,
    libraryDependencies ++= circe,
    inThisBuild(List(
      organization := "com.spikerlabs",
      scalaVersion := "2.12.7",
      version      := "0.1.0-SNAPSHOT"
    )),
    name := "service"
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

lazy val api = (project in file("api")).
  enablePlugins(CucumberPlugin).
  settings(
    libraryDependencies ++= http4s,
    libraryDependencies ++= circe,
    inThisBuild(List(
      organization := "com.spikerlabs",
      scalaVersion := "2.12.7",
      version      := "0.1.0-SNAPSHOT"
    )),
    name := "api"
  )
  // test dependencies
  .settings(
    libraryDependencies ++= cucumber,
    libraryDependencies += scalaTest,
    libraryDependencies += scalaCheck,
    CucumberPlugin.monochrome := false,
    CucumberPlugin.glue := "classpath:steps",
    CucumberPlugin.features := List("classpath:features")
  ).dependsOn(service)