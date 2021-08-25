name := "web"

version := "0.0.20"
scalaVersion := "2.13.6"

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play" % "2.8.8",
  "com.github.jwt-scala" %% "jwt-play-json" % "7.1.1",
  "com.limitra.sdk" %% "database" % "0.0.13"
)

logLevel := Level.Error
