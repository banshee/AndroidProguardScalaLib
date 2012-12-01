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

class FullRunTest extends FunSuite with ShouldMatchers {
  test( "should find an existing matching library" ) {
    val cs = new CacheSystem
    val library = cs.libraryMatchingParameters(testConfiguration)
  }
  
  test( "should build the library if necessary, keeping a cache copy and also putting it in the destination" )( pending )
  test( "should copy the cached version to the destination if no changes were detected" )( pending )
  test( "should update the cache if a new library was created" )( pending )

  val testConfiguration = JartenderCacheParameters(
    inputJars = Array( "/foo1.jar", "/foo2.jar" ),
    classFiles = Array( """C:\cygwin\home\james\workspace\AndroidProguardScalaLib\bin""" ),
    cacheDir = "/cacheDir1",
    confDir = "/confDir1",
    proguardProcessedConfFile = "proguardProcessedConfFile",
    cachedJar = "cachedJar",
    outputJar = "outputJar",
    libraryJars = Array( "android.jar", "jar1.jar" ),
    proguardDefaults = "defaults here",
    proguardAdditionsFile = "additionsFile" )
}