import sbt.Keys._
import sbt._
import xerial.sbt.Sonatype._


lazy val commonSettings: Seq[Setting[_]]  = Seq(
  scalaVersion := "2.13.0",
  scalacOptions ++= Seq("-feature", "-unchecked"),
  organization := "net.team2xh",
  publishTo := sonatypePublishTo.value,
  publishMavenStyle := true,
  sonatypeProfileName := "net.team2xh",
  sonatypeProjectHosting := Some(GitHubHosting(user="Tenchi2xh", repository="Scurses", email="tenchi@team2xh.net")),
  developers := List(
    Developer(id = "tenchi", name = "Hamza Haiken", email = "tenchi@team2xh.net", url = url("http://tenchi.me"))
  ),
  licenses := Seq("MIT" -> url("https://github.com/Tenchi2xh/Scurses/blob/master/LICENSE")),
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
    version := "1.0.1",
    libraryDependencies += "com.lihaoyi" %% "fastparse" % "2.1.3",
    mainClass in (Compile, run) := Some("net.team2xh.scurses.examples.GameOfLife")
  )

lazy val onions = (project in file("onions"))
  .settings(commonSettings: _*)
  .dependsOn(scurses)
  .settings(
    name := "onions",
    version := "1.0.1",
    mainClass in (Compile, run) := Some("net.team2xh.onions.examples.ExampleUI")
  )
