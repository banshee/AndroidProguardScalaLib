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
import com.restphone.jartender.TestUtilities._
import com.google.common.io.Files
import com.restphone.jartender.JartenderCacheParameters
import com.restphone.jartender.CacheSystem
import org.scalatest.matchers.Matcher
import org.scalatest.matchers.MatchResult
import com.restphone.jartender.FileFailureValidation.FileFailure
import org.scalatest.matchers.BeMatcher
import com.restphone.jartender.FileFailureValidation
import com.google.common.base.Charsets

trait ValidationNELMatcher extends ShouldMatchers {
  def succeed[FailureT, SuccessT]( fn: SuccessT => Unit = { x: SuccessT => () } ): ValidationNEL[FailureT, SuccessT] => MatchResult = {
    ( x: ValidationNEL[FailureT, SuccessT] ) =>
      {
        x map { fn( _ ) }
        be( 'Success )( x )
      }
  }
}

class ValidationNELBeMatcher[F, S]( moreMatchers: S => Unit ) extends BeMatcher[ValidationNEL[F, S]] {
  def apply( left: ValidationNEL[F, S] ) = {
    left map { moreMatchers( _ ) }
    val msg = left fold ( fail => ( e: NonEmptyList[F] ) => e.list.mkString( "\r" ), succ => ( s: S ) => s )
    MatchResult( left.isSuccess, "failure: " + msg, "was success" )
  }
}

class ProguardConfigurationGenerationTest extends FunSuite with ShouldMatchers with ValidationNELMatcher {
  def successfulFileOp( fn: String => Unit ) = new ValidationNELBeMatcher[FileFailure, String]( fn )

  test( "can generate the right proguard config file with a missing additions" ) {
    configFileForParameters( baseProguardConfiguration ) should be( successfulFileOp { s =>
      s should include( "# additionsFile (The system cannot find the file specified)" )
      s should include( "# defaults here" )
      s should include( "# Inserting proguard additions file" )
    } )
  }

  test( "can generate a proguard file with an additions file" ) {
    val f = File.createTempFile( "prefix", "suffix" )
    f.deleteOnExit
    val magicString = "snark"
    Files.append(magicString, f, Charsets.UTF_8)
    val conf = baseProguardConfiguration.copy( proguardAdditionsFile = f.getPath )
    configFileForParameters( conf ) should be( successfulFileOp { s =>
      s should not include( "The system cannot find the file specified" )
      s should include (magicString)
    } )
  }

  def configFileForParameters( conf: ProguardCacheParameters ) = {
    val cs = new CacheSystem
    val tmpfile = File.createTempFile( "arg0", "arg1" )
    val pr = new ProguardRunner( cs )
    pr.generateConfigFileContents( cs, conf, tmpfile )
  }

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
