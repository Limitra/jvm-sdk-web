name := "web"

version := "0.0.13"
scalaVersion := "2.12.10"

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play" % "2.8.2",
  "com.jason-goodwin" %% "authentikat-jwt" % "0.4.5",
  "com.limitra.sdk" %% "database" % "0.0.8"
)
