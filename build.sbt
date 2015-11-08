name := "Scurses"

version := "1.0"

scalaVersion := "2.11.7"

scalacOptions ++= Seq("-feature", "-unchecked")

libraryDependencies += "com.lihaoyi" %% "fastparse" % "0.2.1"

mainClass in (Compile, run) := Some("net.team2xh.scurses.examples.StressTest")