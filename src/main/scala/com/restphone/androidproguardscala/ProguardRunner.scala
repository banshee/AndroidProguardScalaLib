package com.restphone.androidproguardscala

import proguard._
import java.io.File
import java.util.Properties

class ProguardRunner( configFile: File, props: Properties = System.getProperties ) {
  def execute = {
    val cparser = new ConfigurationParser( configFile, props )
    val config = new Configuration
    cparser.parse( config )
    val p = new ProGuard( config )
    p.execute
  }
}
