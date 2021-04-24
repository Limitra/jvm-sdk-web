name := "web"

version := "0.0.19"
scalaVersion := "2.13.5"

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play" % "2.8.7",
  "com.github.jwt-scala" %% "jwt-play-json" % "7.1.1",
  "com.limitra.sdk" %% "database" % "0.0.12"
)

logLevel := Level.Error
