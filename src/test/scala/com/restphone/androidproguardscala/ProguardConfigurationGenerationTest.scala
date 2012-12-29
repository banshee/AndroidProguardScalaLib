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
import com.restphone.jartender.RichFile._
import com.restphone.androidproguardscala.TestUtilities._
import com.google.common.io.Files
import com.restphone.jartender.JartenderCacheParameters
import com.restphone.jartender.CacheSystem

class ProguardConfigurationGenerationTest extends FunSuite with ShouldMatchers {
  test( "can generate the right proguard config file" ) {
    val cs = new CacheSystem
    val tmpfile = File.createTempFile( "arg0", "arg1" )
    val conf = createTestConfiguration
    val pr = new ProguardRunner( cs )
    val result = pr.generateConfigFileContents( cs, conf, tmpfile )
    println("hereiamdf")
    println(result.getOrElse("fnord"))
    result should be ('notTested)
  }

  val createTestConfiguration = JartenderCacheParameters(
    inputJars = getResource( "jarfiles/libjar.jar" ).toArray,
    classFiles = getResource( "." ).toArray,
    cacheDir = Files.createTempDir.toString,
    proguardProcessedConfFile = "proguardProcessedConfFile",
    outputJar = new File( Files.createTempDir, "outputjar.jar" ).getPath,
    libraryJars = Array.empty,
    proguardDefaults = "# defaults here",
    proguardAdditionsFile = "additionsFile" )
}
