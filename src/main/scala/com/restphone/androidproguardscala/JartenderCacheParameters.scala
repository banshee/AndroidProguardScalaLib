package com.restphone.androidproguardscala

case class JartenderCacheParameters(
  classFiles: Array[String], // Files containing application code, directories and/or jar files; used in proguard -injar option.  The class files for your application go here.
  cacheDir: String, // Stores the cached jar files and the text files used to calculate signatures.  APS can also clear it on clean builds, so it should be a dedicated directory.
  proguardProcessedConfFile: String, // The proguard configuration file created by the build.  It's a full proguard configuration and can be used outside APS.
  outputJar: String, // Final destination for the jar file.  ABS runs proguard with a destination of cachedJar, and then copies the correct cached output file into outputJar.  (There are many cachedJar files, and only one outputJar)
  inputJars: Array[String], // Written to the proguard file as -injar options, before -outjar.  (Order in the proguard file is what makes only scala libraries appear in the output.)
  libraryJars: Array[String] = Array(), // passed to proguard -libraryjars.  Should always include android.jar.
  proguardDefaults: String = "", // an arbitrary string added to the proguard conf file
  proguardAdditionsFile: String = "" // Path to file added to configuration - can be ""
)