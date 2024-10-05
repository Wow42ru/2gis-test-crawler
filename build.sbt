name := "simple-crawler"

version := "0.1.0"
val http4sVersion = "0.23.28"
val circeVersion = "0.14.9"

scalaVersion := "2.13.14"

libraryDependencies ++= Seq(
  "org.typelevel" %% "cats-core" % "2.12.0",
  "org.typelevel" %% "cats-effect" % "3.5.4",
  "org.http4s" %% "http4s-ember-client" % http4sVersion,
  "org.http4s" %% "http4s-ember-server" % http4sVersion,
  "org.http4s" %% "http4s-dsl" % http4sVersion,
  "org.http4s" %% "http4s-circe" % http4sVersion,
  "io.circe" %% "circe-generic" % circeVersion,
  "io.circe" %% "circe-parser" % circeVersion,
  "org.jsoup" % "jsoup" % "1.18.1",
  "ch.qos.logback" % "logback-classic" % "1.5.0",
  "com.github.pureconfig" %% "pureconfig" % "0.17.1",
  "org.scalatest" %% "scalatest" % "3.2.19" % "test",

)
scalacOptions ++= Seq(
  "-encoding", "utf-8",
  "-explaintypes",
  "-Xcheckinit",
  //"-Xfatal-warnings",
  //"-Xlint:unused"
)