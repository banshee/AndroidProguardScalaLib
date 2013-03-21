offline in Test := true

name := "AndroidProguardScalaLib"

organization := "com.restphone"

version := "0.4"

scalaVersion := "2.10.1"

publishMavenStyle := true

libraryDependencies ++= Seq(
  "com.google.guava" % "guava" % "13.0.1",
  "net.sf.proguard" % "proguard-base" % "4.8",
  "com.restphone" %% "jartender" % "0.5",
  "org.scalaz" %% "scalaz-core" % "7.0.0-M7",
  "org.scalatest" %% "scalatest" % "2.0.M5b" % "test",
  "com.restphone" %% "scalatestutilities" % "0.4-SNAPSHOT" % "test"
)

publishArtifact in Test := false

pomExtra := (
  <url>https://github.com/banshee/AndroidProguardScalaLib</url>
  <licenses>
    <license>
      <name>GPL</name>
      <url>https://github.com/banshee/AndroidProguardScalaLib/blob/master/COPYING</url>
      <distribution>repo</distribution>
    </license>
  </licenses>
  <scm>
    <url>git:git@github.com:banshee/AndroidProguardScalaLib.git</url>
    <connection>scm:git:git@github.com:banshee/AndroidProguardScalaLib.git</connection>
  </scm>
  <developers>
    <developer>
      <id>jamesmoore</id>
      <name>James Moore</name>
      <organization>RESTPhone</organization>
      <organizationUrl>http://restphone.com</organizationUrl>
    </developer>
  </developers>)

