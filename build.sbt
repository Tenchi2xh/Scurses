name := "Scurses"

version := "1.0"

scalaVersion := "2.11.7"

libraryDependencies += "org.scala-lang" % "jline" % "2.9.0-1"
scalacOptions ++= Seq("-feature")

mainClass in (Compile, run) := Some("net.team2xh.scurses.examples.StressTest")