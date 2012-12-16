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
import com.restphone.jartender.FileFailureValidation._
import com.google.common.base.Charsets
import scala.PartialFunction._

class FullRunTest extends FunSuite with ShouldMatchers {
  test( "should find an existing matching library" ) {
    val cs = new CacheSystem
    val library = cs.findInCache( createTestConfiguration )
    library should be( 'defined )
  }

  def getResource( s: String ) = {
    val extractFilePathExpr = """file:/(.*)""".r
    val root = Option( Thread.currentThread.getContextClassLoader.getResource( s ) )
    root flatMap { condOpt( _ ) { case extractFilePathExpr( f ) => f } }
  }

  test( "should build the library if necessary, keeping a cache copy and also putting it in the destination" ) {
    val conf = createTestConfiguration
    val cs = new CacheSystem
    val library = cs.findInCache( conf )
    library should not be ( 'defined )
    library getOrElse {
      val cacheDir = validDirectory( new File( conf.cacheDir ), "cache directory" )
      val configfilename = File.createTempFile( "jartender_proguard", ".conf", cacheDir.getOrElse( new File( "/tmp" ) ) )
      val checksum = "xxx"
      val cachedJar = File.createTempFile( f"jartender_cache_${checksum}", ".jar", cacheDir.getOrElse( new File( "/tmp" ) ) )
      val configFile = ProguardConfigFileGenerator.generateConfigFile( cs, conf, cachedJar, configfilename )
      val p = new ProguardRunner( configfilename )
      p.execute
      cs.cacheEntryForProcessedLibrary( conf, new File( conf.outputJar ) ) map { newCacheEntry =>
        cs.addCacheEntry( newCacheEntry )
        if ( !Files.equal( cachedJar, new File( conf.outputJar ) ) ) {
          Files.copy( cachedJar, new File( conf.outputJar ) )
        }
      }
    }
  }

  //  test( "full cycle" ) {
  //    val ops =
  //      "seq[existing jar that matches, a new built jar that also gets inserted into the cache]"
  //    "find the first match"
  //    "copy the first match into outputJar"
  //    "return the status"
  //  }

  test( "should copy the cached version to the destination if no changes were detected" )( pending )

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