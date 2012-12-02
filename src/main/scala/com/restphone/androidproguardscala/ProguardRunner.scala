package com.restphone.androidproguardscala

import proguard._
import java.io.File

class ProguardRunner( configFile : File ) {
  def execute = {
    val cparser = new ConfigurationParser( configFile )
    val config = new Configuration
    cparser.parse( config )
    val p = new ProGuard( config )
    p.execute
  }
}
