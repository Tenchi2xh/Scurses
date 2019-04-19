import xerial.sbt.Sonatype._

publishMavenStyle := true

sonatypeProfileName := "net.team2xh"
sonatypeProjectHosting := Some(GitHubHosting(user="Tenchi2xh", repository="Scurses", email="tenchi@team2xh.net"))
developers := List(
  Developer(id = "tenchi", name = "Hamza Haiken", email = "tenchi@team2xh.net", url = url("http://tenchi.me"))
)
licenses := Seq("MIT" -> url("https://github.com/Tenchi2xh/Scurses/blob/master/LICENSE"))
