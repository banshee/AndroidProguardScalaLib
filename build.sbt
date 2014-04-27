offline in Test := true

name := "AndroidProguardScalaLib"

organization := "com.restphone"

version := "0.6-SNAPSHOT"

// "com.restphone" %% "androidproguardscalalib" % "0.6-SNAPSHOT"

scalaVersion := "2.11.0"

publishMavenStyle := true

libraryDependencies ++= Seq(
  "net.sf.proguard" % "proguard-base" % "4.10",
  "com.google.guava" % "guava" % "16.0.1",
  "com.restphone" %% "scalatestutilities" % "0.6-SNAPSHOT",
  "com.restphone" %% "jartender" % "0.7-SNAPSHOT",
  "com.restphone" %% "scalatestutilities" % "0.5" % "test",
  "org.scalaz" %% "scalaz-core" % "7.0.6"
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

