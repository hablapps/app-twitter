name := "app-twitter"

version := "1.0"

organization := "org.hablapps"

scalaVersion := "2.10.0"

scalacOptions ++= Seq("-feature")

libraryDependencies ++= Seq(
  "org.scala-lang" % "scala-reflect" % "2.10.0",
  "org.scalatest" % "scalatest_2.10" % "1.9.1" % "test",
  "junit" % "junit" % "4.10" % "test"
)

exportJars := true


