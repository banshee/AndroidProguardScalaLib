package com.restphone.androidproguardscala

import org.scalatest.matchers.ShouldMatchers
import org.scalatest.FunSuite
import scala.annotation.tailrec
import scala.collection._
import scala.collection.mutable.SynchronizedMap
import scala.collection.mutable
import scala.util.Random.shuffle
import scalaz._
import Scalaz._
import java.io.File
import com.restphone.androidproguardscala.RichFile._

class ProguardConfigurationGenerationTest extends FunSuite with ShouldMatchers {
  test( "can generate the right proguard config file" ) {
    val testConf = JartenderCacheParameters(
      inputJars = Array( "/foo1.jar", "/foo2.jar" ),
      classFiles = Array( """C:\cygwin\home\james\workspace\AndroidProguardScalaLib\bin""" ),
      cacheDir = "/cacheDir1",
      proguardProcessedConfFile = "proguardProcessedConfFile",
      outputJar = "outputJar",
      libraryJars = Array( "android.jar", "jar1.jar" ),
      proguardDefaults = "defaults here",
      proguardAdditionsFile = "additionsFile" )

    val cs = new CacheSystem

    val tmpfile = File.createTempFile("arg0", "arg1")
    val result = ProguardConfigFileGenerator.generateConfigFileContents( cs, testConf, tmpfile )
  }

  test( "can generate the right set of Provides* and Uses*" ) {
  }
}
