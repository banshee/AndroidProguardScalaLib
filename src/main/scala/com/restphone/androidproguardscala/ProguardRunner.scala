package com.restphone.androidproguardscala

import java.io.File
import java.io.IOException
import java.util.Properties
import scala.Array.apply
import scala.collection.immutable.List.apply
import com.google.common.base.Charsets
import com.google.common.io.Files
import com.restphone.jartender.CacheSystem
import com.restphone.jartender.FileFailureValidation._
import com.restphone.jartender.ProvidesClass
import proguard.Configuration
import proguard.ConfigurationParser
import proguard.ProGuard
import scalaz._
import Scalaz._
import scala.util.control.Exception._
import proguard.ParseException
import com.restphone.jartender.Shrinker

class ProguardRunner( cacheSystem: CacheSystem, props: Properties = System.getProperties ) {
  import com.restphone.androidproguardscala.ProguardCacheParameters._

  def createShrinker( conf: ProguardCacheParameters ) = new Shrinker {
    def jartenderCacheParameters = conf.jartenderConfiguration
    def execute = ProguardRunner.this.execute( conf )
  }

  def execute( conf: ProguardCacheParameters ): FailureValidation[File] = {
    for {
      cacheDir <- validDirectory( new File( conf.jartenderConfiguration.cacheDir ), "cache directory" )
      configfilename <- (new File(cacheDir, "proguard_processed.conf" )).success
      cachedJar <- validatedTempFile( "cachedJar file", "jartender_cache_", ".jar", cacheDir )
      configFile <- generateConfigFile( conf, cachedJar, configfilename )
      shrinkerOutput <- executeProguard( new File( configFile.getPath ) )
      validatedOutputJar <- validatedFile( cachedJar, "Proguard did not produce the expected output" )
    } yield validatedOutputJar
  }

  private def executeProguard( configFile: File ): FailureValidation[Unit] = {
    implicit val localExceptions = classOf[ParseException] :: stdExceptions

    //    def catchExceptions( contextMsg: String ) = {
    //      val parseException = catching( classOf[ParseException] ) withApply { x => ( FailureValidation( contextMsg, x.getLocalizedMessage ) ).failNel }
    //      convertExceptions( contextMsg ) or parseException
    //    }
    //
    for {
      existingConfigFile <- validatedFile( configFile, "reading proguard config file" )
      cparser <- convertExceptions( "creating configuration parser" )( new ConfigurationParser( existingConfigFile, props ).success )
      config = new Configuration
      _ <- convertExceptions( "parsing proguard configuration file" )( cparser.parse( config ).success )
      proguard = new ProGuard( config )
      result <- convertExceptions( "executing Proguard" )( proguard.execute.success )
    } yield result
  }

  def generateConfigFileContents( cache: CacheSystem, c: ProguardCacheParameters, cachedJarLocation: File ) = {
    // input jars
    // output jar
    // classfiles (as library jars)
    // library jars
    // defaults (fixed text)
    // proguard_additions.conf
    // keep every entrypoint from user's classfiles
    def quote( s: String ) = "\"" + s + "\""

    val classfiles = c.jartenderConfiguration.classFiles map quote map { s => f"-injars $s" }
    val inputjars = c.jartenderConfiguration.inputJars map quote map { s => f"-injars $s(!META-INF/MANIFEST.MF)" }
    val outputjar = Array( "-outjars " + quote( cachedJarLocation.getPath ) )
    val libraryjars = c.jartenderConfiguration.libraryJars map quote map { s => f"-libraryjars $s" }
    val proguardAdditionsFile = List( f"# Inserting proguard additions file ${c.proguardAdditionsFile} here", fileContentsOrExceptionMessage( new File( c.proguardAdditionsFile ) ) )
    val builtinOptions = Array( c.proguardDefaults )

    keepOptionsForClassfiles( cache, c ) map { keepOptions =>
      val combined = inputjars ++ outputjar ++ classfiles ++ libraryjars ++ builtinOptions ++ proguardAdditionsFile ++ keepOptions

      combined.mkString( "\n" )
    }
  }

  def generateConfigFile( c: ProguardCacheParameters, cachedJarLocation: File, outputLocation: File ) = {
    generateConfigFileContents( cacheSystem, c, cachedJarLocation ) map { contents =>
      Files.write( contents, outputLocation, Charsets.UTF_8 )
      outputLocation
    }
  }

  def keepOptionsForClassfiles( cache: CacheSystem, p: ProguardCacheParameters ) =
    cache.elementsFromClassfiles( p.jartenderConfiguration ) map {
      _ collect
        { case ProvidesClass( _, _, internalName, _, _, _ ) => internalName.javaIdentifier } map
        { x => f"-keep class ${x.s} {*;}" }
    }

  private def fileContentsOrExceptionMessage( f: File ) = {
    try {
      Files.toString( f, Charsets.UTF_8 )
    } catch {
      case e: IOException => f"# ${e.getLocalizedMessage}".replace( '\n', ' ' )
    }
  }
}

