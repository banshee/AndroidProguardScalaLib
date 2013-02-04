// use sbt deliver-local to create ivy.xml

name := "AndroidProguardScalaLib"

organization := "com.restphone"

version := "0.3-SNAPSHOT"

scalaVersion := "2.10.0"

libraryDependencies ++= Seq(
  "net.sf.proguard" % "proguard-base" % "4.8",
  "com.restphone" %% "jartender" % "latest.snapshot",
  "com.restphone" %% "scalatestutilities" % "latest.snapshot" % "test",
  "org.scala-lang" % "scala-actors" % "2.10.0",
  "org.scalatest" %% "scalatest" % "latest.snapshot" % "test",
  "org.scalaz" %% "scalaz-core" % "latest.snapshot"
)
