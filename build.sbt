import com.jsuereth.sbtpgp.PgpKeys.publishSigned
import sbt.Keys._
import sbt._
import xerial.sbt.Sonatype._

ThisBuild / scalaVersion := "2.13.0"
ThisBuild / scalacOptions ++= Seq("-feature", "-unchecked")
ThisBuild / organization := "net.team2xh"
publishTo := sonatypePublishTo.value
publishMavenStyle := true
ThisBuild / sonatypeProfileName := "net.team2xh"
ThisBuild / sonatypeProjectHosting := Some(GitHubHosting(user="Tenchi2xh", repository="Scurses", email="tenchi@team2xh.net"))
ThisBuild / developers := List(
  Developer(id = "tenchi", name = "Hamza Haiken", email = "tenchi@team2xh.net", url = url("http://tenchi.me"))
)
ThisBuild / licenses := Seq("MIT" -> url("https://github.com/Tenchi2xh/Scurses/blob/master/LICENSE"))

lazy val root = (project in file("."))
  .aggregate(scurses, onions)
  .settings(
    publish / skip := true,
    publishLocal / skip := true,
    publishSigned / skip := true
  )

lazy val scurses = (project in file("scurses"))
  .settings(
    name := "scurses",
    version := "1.0.1",
    libraryDependencies += "com.lihaoyi" %% "fastparse" % "2.1.3",
    Compile / run / mainClass := Some("net.team2xh.scurses.examples.GameOfLife")
  )

lazy val onions = (project in file("onions"))
  .dependsOn(scurses)
  .settings(
    name := "onions",
    version := "1.0.1",
    Compile / run / mainClass := Some("net.team2xh.onions.examples.ExampleUI")
  )
