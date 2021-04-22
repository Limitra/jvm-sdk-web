name := "web"

version := "0.0.18"
scalaVersion := "2.13.5"

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play" % "2.8.7",
  "com.github.jwt-scala" %% "jwt-play-json" % "7.1.1",
  "com.limitra.sdk" %% "database" % "0.0.11"
)

logLevel := Level.Error
