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

class ProguardConfigurationGenerationTest extends FunSuite with ShouldMatchers {
  test( "can generate the right proguard config file" ) {
    val testConf = ProguardCacheParameters(
      inputJars = Array( "/foo1.jar", "/foo2.jar" ),
      classFiles = Array( "/cf1", "/cf2" ),
      cacheDir = "/cacheDir1",
      confDir = "/confDir1",
      proguardProcessedConfFile = "proguardProcessedConfFile",

      cachedJar = "cachedJar",

      outputJar = "outputJar",

      libraryJars = Array( "android.jar", "jar1.jar" ),
      proguardDefaults = "defaults here",
      proguardAdditionsFile = "additionsFile" )

    val result = ProguardConfigFileGenerator.generateConfigFileContents( testConf )

    println( result )

    val t = ProguardConfigFileGenerator.tree( new File( "c:/Users/james/t1" ), x => true )
    println(t)
    t.toList foreach { println( _ ) }
  }
}
