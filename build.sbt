name := "chatty"
version := "0.1.0-SNAPSHOT"

scalaVersion in ThisBuild := "2.11.8"

lazy val commonSettings = Seq(
  scalacOptions ++= Seq(
    "-deprecation",
    "-unchecked",
    "-feature",
    "-Xfatal-warnings",
    "-Xfuture",
    "-Yno-adapted-args"
  )
)

lazy val root = 
  (project in file("."))
    .settings(
      run in Compile <<= run in Compile in server
    )
    .aggregate(core, server)

lazy val server =
  (project in file("server"))
    .settings(commonSettings: _*)
    .settings(
      libraryDependencies ++= Seq(
        "com.typesafe.akka" %% "akka-http-experimental" % "2.4.11" 
      )
    )
    .dependsOn(core)

lazy val core =
  (project in file("core"))
    .settings(commonSettings: _*)
