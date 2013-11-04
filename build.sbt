name := "app-twitter"

version := "1.0"

organization := "org.hablapps"

scalaVersion in ThisBuild := "2.10.2"

scalacOptions ++= Seq("-feature")

resolvers ++= Seq(
  "Speech repo releases" at "http://andromeda/repo/releases",
  "Speech repo snapshots" at "http://andromeda/repo/snapshots",
  "Speech private-repo snapshots" at "http://andromeda/private-repo/snapshots"
)

libraryDependencies in ThisBuild ++= Seq(
    "org.scalatest" % "scalatest_2.10" % "1.9.1" % "test",
    "org.hablapps" %% "speech-web" % "0.7.1-20131103230650-9403"
)

exportJars := true
