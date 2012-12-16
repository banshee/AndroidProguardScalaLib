package com.restphone.androidproguardscala

import proguard._
import java.io.File
import java.util.Properties
import com.restphone.jartender.FileFailureValidation._
import scalaz._
import Scalaz._

class ProguardRunner( configFile: File, props: Properties = System.getProperties ) {
  case class ProguardResult( val configFile: File )

  def execute: FileFailureValidation[ProguardResult] = {
    val cparser = new ConfigurationParser( configFile, props )
    val config = new Configuration
    cparser.parse( config )
    val p = new ProGuard( config )
    p.execute
    ProguardResult(configFile).success
  }
}
