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
import org.scalatest.junit.JUnitRunner

class CacheTest extends FunSuite with ShouldMatchers {
  test( "can generate cache file" ) {
    val tmpdir = Files.createTempDir
    val file1 = new File( tmpdir, "one.jar" )
    Files.write( "from", file1, Charsets.UTF_8 )

    val cachefile = new File( tmpdir, "cache.1.cache" )

    val usesSnark = UsesClass( JavaIdentifier( "com.restphone.Snark" ) )
    val usesBoojum = UsesClass( JavaIdentifier( "com.restphone.Boojum" ) )

    val pfi = ProviderFilesInformation.createFromFiles( List( file1 ) )
    pfi should be ('success)

    val cachent = CacheEntry( usesItems = Set( usesSnark, usesBoojum ),
      providerFileInformation = pfi.toOption.get,
      jarfilepath = file1.getPath )

    val cache = Cache( Set( cachent ) )

    val bytesForCache = SerializableUtilities.converToByteArray( cache )

    Files.write( bytesForCache, cachefile )

    val bytesFromFile = Files.toByteArray( cachefile )

    val tst: Option[Cache] = SerializableUtilities.byteArrayToObject( bytesFromFile )
    tst should be ('defined)
  }

  test( "can find an item in the cache" ) {
    val tmpdir = Files.createTempDir
    val providerFile = new File( tmpdir, "one.jar" )
    Files.write( "from", providerFile, Charsets.UTF_8 )
    
    val pfi = ProviderFilesInformation.createFromFiles( List( providerFile ) )
    pfi should be ('success)

    // We need to put something into the file so it exists,
    // but it doesn't need to be a real jar.  The disk cache
    // relies on file checksums.gt

    val cachefile = new File( tmpdir, "cache.1.cache" )

    val usesSnark = UsesClass( JavaIdentifier( "com.restphone.Snark" ) )
    val usesBoojum = UsesClass( JavaIdentifier( "com.restphone.Boojum" ) )

    val cacheEntry = CacheEntry( usesItems = Set( usesSnark, usesBoojum ),
      providerFileInformation = pfi.toOption.get,
      jarfilepath = providerFile.getPath )

    val cache = Cache( Set( cacheEntry ) )

    val bytesForCache = SerializableUtilities.converToByteArray( cache )

    Files.write( bytesForCache, cachefile )

    val bytesFromCacheFile = Files.toByteArray( cachefile )

    val cacheReadFromFile: Option[Cache] = SerializableUtilities.byteArrayToObject( bytesFromCacheFile )
    cacheReadFromFile should be ('defined)

    val cacheResult = cacheReadFromFile.get.findInCache( Set( usesBoojum ), pfi.toOption.get )
    cacheResult should equal( some(cacheEntry) )
  }
}
