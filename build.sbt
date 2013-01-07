// use sbt deliver-local to create ivy.xml

offline in Test := true

name := "AndroidProguardScalaLib"

organization := "com.restphone"

version := "0.1-SNAPSHOT"

scalaVersion := "2.10.0-RC3"

scalaBinaryVersion <<= scalaVersion

// Compile for these Scala versions
crossScalaVersions := Seq("2.10.0-RC1")

publishMavenStyle := true

externalResolvers := Seq(
  "RESTPhone Nexus" at "http://git:8081/nexus/content/groups/public",
  Resolver.sonatypeRepo("snapshots")
)

transitiveClassifiers := Seq("sources")

libraryDependencies ++= Seq(
  "com.restphone" %% "jartender" % "0.3-SNAPSHOT",
  "com.restphone" %% "scalatestutilities" % "latest.snapshot" % "test",
  "org.scalaz" % "scalaz-core_2.10.0-RC3" % "7.0.0-M5",
  "net.sf.proguard" % "proguard-base" % "4.8"
)

libraryDependencies ++= Seq(
  "org.scalatest" % "scalatest_2.10.0-RC3" % "2.0.M5-B1"
)

publishTo <<= version { (v: String) =>
  val nexus = "http://git:8081/nexus/content/repositories/"
  if (v.trim.endsWith("SNAPSHOT"))
    Some("snapshots" at nexus + "snapshots")
  else
    Some("releases" at nexus + "releases")
}

credentials += Credentials("Sonatype Nexus Repository Manager", 
                           "git", 
                           "deployment",
                           "deploy")
