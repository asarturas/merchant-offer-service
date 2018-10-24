import sbt._

object Dependencies {
  lazy val scalaTest = "org.scalatest" %% "scalatest" % "3.0.5" % Test
  lazy val scalaCheck = "org.scalacheck" %% "scalacheck" % "1.14.0" % Test
  lazy val cucumber = Seq(
    "io.cucumber" % "cucumber-core",
    "io.cucumber" %% "cucumber-scala",
    "io.cucumber" % "cucumber-jvm",
    "io.cucumber" % "cucumber-junit",
  ).map(_ % "2.0.1" % Test)
}
