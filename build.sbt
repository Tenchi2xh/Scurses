import sbt.Keys._
import sbt._


lazy val commonSettings: Seq[Setting[_]]  = Seq(
  scalaVersion := "2.12.8",
  scalacOptions ++= Seq("-feature", "-unchecked"),
  organization := "net.team2xh",
  publishTo := sonatypePublishTo.value,
  useGpg := true
)

lazy val root = (project in file("."))
  .settings(commonSettings: _*)
  .aggregate(scurses, onions)
  .settings(skip in publish := true)

lazy val scurses = (project in file("scurses"))
  .settings(commonSettings: _*)
  .settings(
    name := "scurses",
    version := "1.0.0",
    libraryDependencies += "com.lihaoyi" %% "fastparse" % "2.1.0",
    mainClass in (Compile, run) := Some("net.team2xh.scurses.examples.GameOfLife")
  )

lazy val onions = (project in file("onions"))
  .settings(commonSettings: _*)
  .dependsOn(scurses)
  .settings(
    name := "onions",
    version := "1.0.0",
    mainClass in (Compile, run) := Some("net.team2xh.onions.examples.ExampleUI")
  )
