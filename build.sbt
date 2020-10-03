name := "web"

version := "0.0.2"
scalaVersion := "2.12.8"

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play" % "2.7.2",
  "com.jason-goodwin" %% "authentikat-jwt" % "0.4.5",
  "com.sksamuel.scrimage" %% "scrimage-core" % "3.0.0-alpha4",
  "com.sksamuel.scrimage" %% "scrimage-io-extra" % "3.0.0-alpha4",
  "com.sksamuel.scrimage" %% "scrimage-filters" % "3.0.0-alpha4",
  "com.limitra.sdk" %% "database" % "0.0.2"
)
