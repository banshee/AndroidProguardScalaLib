package com.restphone.androidproguardscala

import proguard._

class ProguardRunner(configFile: File) {
  val c = new ConfigurationParser(configFile)
}