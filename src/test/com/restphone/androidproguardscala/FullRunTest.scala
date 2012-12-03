package com.restphone.androidproguardscala

import org.scalatest.matchers.ShouldMatchers
import org.scalatest.FunSuite
import scala.annotation.tailrec
import scala.util.Random.shuffle
import scalaz._
import Scalaz._
import java.io.File
import com.restphone.androidproguardscala.RichFile._
import com.google.common.io.Files
import com.restphone.jartender.UsesClass
import com.restphone.jartender._
import com.google.common.base.Charsets

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

@RunWith( classOf[JUnitRunner] )
class FullRunTest extends FunSuite with ShouldMatchers {
  test( "should find an existing matching library" ) {
    val cs = new CacheSystem
    val library = cs.libraryMatchingParameters( testConfiguration )
    library should be( 'defined )
  }

  def validDirectory( f: File, failureMsg: String ) = if ( f.exists && f.isDirectory ) f.success else ( f.getPath + " is not a valid directory: " + failureMsg ).failure
  def convertStringToValidatedDirectory( s: String, failureMsg: String = "" ) = validDirectory( new File( s ), failureMsg )

  test( "should build the library if necessary, keeping a cache copy and also putting it in the destination" ) {
    val cs = new CacheSystem
    val library = cs.libraryMatchingParameters( testConfiguration )
    library should not be ( 'defined )
    library getOrElse {
      val cacheDir = validDirectory( new File( testConfiguration.cacheDir ), "cache directory" )
      val configfilename = File.createTempFile( "jartender_proguard", ".conf", cacheDir.getOrElse( new File( "/tmp" ) ) )
      val checksum = "xxx"
      val cachedJar = File.createTempFile( f"jartender_cache_${checksum}", ".jar", cacheDir.getOrElse( new File( "/tmp" ) ) )
      val configFile = ProguardConfigFileGenerator.generateConfigFile( cs, testConfiguration, cachedJar, configfilename )
      val p = new ProguardRunner( configfilename )
      p.execute
      val newCacheEntry = cs.cacheEntryForProcessedLibrary( testConfiguration, new File( testConfiguration.outputJar ) )
      cs.addCacheEntry( newCacheEntry )
      if ( !Files.equal( cachedJar, testConfiguration.outputJar ) ) {
        Files.copy( cachedJar, testConfiguration.outputJar )
      }
    }
  }

  test( "full cycle" ) {
    val ops =
      "seq[existing jar that matches, a new built jar that also gets inserted into the cache]"
    "find the first match"
    "copy the first match into outputJar"
    "return the status"
  }

  test( "should copy the cached version to the destination if no changes were detected" )( pending )

  val testConfiguration = JartenderCacheParameters(
    inputJars = Array( "/foo1.jar", "/foo2.jar" ),
    classFiles = Array( """C:\cygwin\home\james\workspace\AndroidProguardScalaLib\bin""" ),
    cacheDir = "/cacheDir1",
    proguardProcessedConfFile = "proguardProcessedConfFile",
    outputJar = "outputJar",
    cachedJar = "cachedJar",
    libraryJars = Array( "android.jar", "jar1.jar" ),
    proguardDefaults = "defaults here",
    proguardAdditionsFile = "additionsFile" )
}