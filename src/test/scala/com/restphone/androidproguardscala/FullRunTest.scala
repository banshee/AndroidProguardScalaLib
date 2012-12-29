package com.restphone.androidproguardscala

import java.io.File
import scala.Option.option2Iterable
import scala.Some.apply
import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers
import com.google.common.io.Files
import com.restphone.jartender.TestUtilities.getResource
import com.restphone.jartender.BuiltLibrary
import com.restphone.jartender.CacheSystem
import com.restphone.jartender.ExistingLibrary
import com.restphone.jartender.JartenderCacheParameters.apply
import scalaz._
import Scalaz._
import com.restphone.jartender.JartenderCacheParameters

class FullRunTest extends FunSuite with ShouldMatchers {
  test( "should not find a match when there's nothing in the cache" ) {
    val cs = new CacheSystem
    val library = cs.findInCache( baseProguardConfiguration.jartenderConfiguration )
    library should be( Success( None ) )
  }

  test( "should build the library if necessary, keeping a cache copy and also putting it in the destination" ) {
    val conf = baseProguardConfiguration
    val cs = new CacheSystem
    val shrinker = new DummyShrinker(conf.jartenderConfiguration)
    val first = {
      val result = cs.execute( shrinker )
      result.toOption collect { case x: BuiltLibrary => true } should be( Some( true ) )
    }
    val second = {
      val result = cs.execute( shrinker )
      result.toOption collect { case x: ExistingLibrary => true } should be( Some( true ) )
    }
  }

  test( "should copy the cached version to the destination if no changes were detected" )( pending )

  lazy val baseProguardConfiguration = ProguardCacheParameters(
    jartenderConfiguration = baseJartenderConfiguration,
    proguardProcessedConfFile = "proguardProcessedConfFile",
    proguardDefaults = "# defaults here",
    proguardAdditionsFile = "additionsFile" )

  lazy val baseJartenderConfiguration = JartenderCacheParameters(
    inputJars = List( getResource( "jarfiles/libjar.jar" ) ),
    classFiles = List( getResource( "." ) ),
    cacheDir = Files.createTempDir.toString,
    libraryJars = List.empty )
}