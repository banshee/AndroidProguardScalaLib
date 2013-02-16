package com.restphone.androidproguardscala

import com.restphone.jartender.JartenderCacheParameters

case class ProguardCacheParameters(
  jartenderConfiguration: JartenderCacheParameters,
  proguardProcessedConfFile: String, // The proguard configuration file created by the build.  It's a full proguard configuration and can be used outside APS.
  proguardDefaults: String = """-dontwarn
-dontoptimize
-dontobfuscate
-dontskipnonpubliclibraryclasses
-dontskipnonpubliclibraryclassmembers

-ignorewarnings
-forceprocessing
    
-keep public class scala.ScalaObject
-keep public class scala.Function0, scala.Function1

# Fix for https://issues.scala-lang.org/browse/SI-5397 

-keep class scala.collection.SeqLike {
    public protected *;
}
    """, // an arbitrary string added to the proguard conf file
  proguardAdditionsFile: String = "" // Path to file added to configuration - can be ""
  )