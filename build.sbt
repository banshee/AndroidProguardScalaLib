offline in Test := true

name := "AndroidProguardScalaLib"

organization := "com.restphone"

version := "0.4"

scalaVersion := "2.10.1"

publishMavenStyle := true

libraryDependencies ++= Seq(
  "net.sf.proguard" % "proguard-base" % "4.8",
  "com.restphone" %% "jartender" % "0.5",
  "org.scalaz" %% "scalaz-core" % "7.0.0-M7",
  "org.scalatest" %% "scalatest" % "2.0.M5b" % "test",
  "com.restphone" %% "scalatestutilities" % "0.4-SNAPSHOT" % "test"
)

publishTo <<= version { (v: String) =>
  val nexus = "https://oss.sonatype.org/"
  if (v.trim.endsWith("SNAPSHOT"))
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases"  at nexus + "service/local/staging/deploy/maven2")
}

publishArtifact in Test := false

libraryDependencies ++= Seq(
  "org.scalaz" %% "scalaz-core" % "7.0.0-M7",
  "com.google.guava" % "guava" % "13.0.1",
  "org.scalatest" %% "scalatest" % "2.0.M5b" % "test"
)

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

