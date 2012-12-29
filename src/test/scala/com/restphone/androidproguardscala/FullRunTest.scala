package com.restphone.androidproguardscala

import org.scalatest.matchers.ShouldMatchers
import org.scalatest.FunSuite
import scala.annotation.tailrec
import scala.util.Random.shuffle
import scalaz._
import Scalaz._
import java.io.File
import com.restphone.jartender.RichFile._
import com.google.common.io.Files
import com.restphone.jartender.UsesClass
import com.restphone.jartender._
import com.restphone.jartender.FileFailureValidation._
import com.google.common.base.Charsets
import scala.PartialFunction._
import com.restphone.androidproguardscala.TestUtilities._
import com.restphone.jartender.ExistingLibrary
import com.restphone.jartender.CacheSystem
import com.restphone.jartender.BuiltLibrary

class FullRunTest extends FunSuite with ShouldMatchers {
  test( "should not find a match when there's nothing in the cache" ) {
    val cs = new CacheSystem
    val library = cs.findInCache( createTestConfiguration )
    library should be( Success( None ) )
  }

  test( "should build the library if necessary, keeping a cache copy and also putting it in the destination" ) {
    val conf = createTestConfiguration
    val cs = new CacheSystem
    val shrinker = new DummyShrinker
    val first = {
      val result = cs.execute( conf, shrinker )
      result.toOption collect { case x: BuiltLibrary => true } should be( Some( true ) )
    }
    val second = {
      val result = cs.execute( conf, shrinker )
      result.toOption collect { case x: ExistingLibrary => true } should be( Some( true ) )
    }
  }

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