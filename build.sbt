// use sbt deliver-local to create ivy.xml

name := "AndroidProguardScalaLib"

organization := "com.restphone"

version := "0.4-SNAPSHOT"

scalaVersion := "2.10.0"

publishMavenStyle := true

libraryDependencies ++= Seq(
  "net.sf.proguard" % "proguard-base" % "4.8",
  "com.restphone" %% "jartender" % "0.4-SNAPSHOT",
  "org.scalaz" %% "scalaz-core" % "7.0.0-M7",
  "org.scalatest" %% "scalatest" % "2.0.M5b" % "test",
  "com.restphone" %% "scalatestutilities" % "0.4-SNAPSHOT" % "test"
)
