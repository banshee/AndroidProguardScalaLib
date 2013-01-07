package com.restphone.androidproguardscala

import java.io.File

import org.scalatest.FunSuite
import org.scalatest.matchers.MatchResult
import org.scalatest.matchers.ShouldMatchers

import com.google.common.base.Charsets
import com.google.common.io.Files
import com.restphone.jartender.CacheSystem
import com.restphone.jartender.FileFailureValidation._
import com.restphone.jartender.JartenderCacheParameters
import com.restphone.scalatestutilities.ScalaTestMatchers.ValidationNELBeMatcher
import com.restphone.scalatestutilities.TestUtilities.getResource

import scalaz._
import scalaz.ValidationNEL

import com.restphone.scalatestutilities.HasSuccessValue._

trait ValidationNELMatcher extends ShouldMatchers {
  def succeed[FailureT, SuccessT]( fn: SuccessT => Unit = { x: SuccessT => () } ): ValidationNEL[FailureT, SuccessT] => MatchResult = {
    ( x: ValidationNEL[FailureT, SuccessT] ) =>
      {
        x map { fn( _ ) }
        be( 'Success )( x )
      }
  }
}

class ProguardConfigurationGenerationTest extends FunSuite with ShouldMatchers with ValidationNELMatcher {
  test( "can generate the right proguard config file with a missing additions" ) {
    val x = configFileForParameters( baseProguardConfiguration )
    x.successValue should include( "# additionsFile (The system cannot find the file specified)" )
    x.successValue should include( "# defaults here" )
    x.successValue should include( "# Inserting proguard additions file" )
  }

  test( "can generate a proguard file with an additions file" ) {
    val f = File.createTempFile( "prefix", "suffix" )
    f.deleteOnExit
    val magicString = "snark"
    Files.append( magicString, f, Charsets.UTF_8 )
    val conf = baseProguardConfiguration.copy( proguardAdditionsFile = f.getPath )

    val x = configFileForParameters( conf )
    x.successValue should not include ( "The system cannot find the file specified" )
    x.successValue should include( magicString )
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
