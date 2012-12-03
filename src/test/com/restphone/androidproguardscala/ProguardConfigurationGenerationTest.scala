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

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
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

    val result = ProguardConfigFileGenerator.generateConfigFileContents( cs, testConf )

    println( result )

    val f = splitFile( new File( """\\something\else\\here""" ) )
    println( f )
  }

  test( "can generate the right set of Provides* and Uses*" ) {
  }
}
