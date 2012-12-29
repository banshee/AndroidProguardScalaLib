package com.restphone.androidproguardscala

import proguard._
import java.io.File
import java.util.Properties
import com.restphone.jartender.FileFailureValidation._
import scalaz._
import Scalaz._
import java.io.File
import java.io.IOException
import scala.Array.canBuildFrom
import com.google.common.base.Charsets
import com.google.common.io.Files
import com.restphone.jartender.RichFile.tree
import com.restphone.jartender.DependencyAnalyser
import com.restphone.jartender.ProvidesClass
import com.restphone.jartender.JartenderCacheParameters
import com.restphone.jartender.CacheSystem
import com.restphone.jartender.Shrinker

class ProguardRunner(  cacheSystem: CacheSystem, props: Properties = System.getProperties ) extends Shrinker {
  def execute( conf: JartenderCacheParameters ): FileFailureValidation[File] = {
    for {
      cacheDir <- validDirectory( new File( conf.cacheDir ), "cache directory" )
      configfilename <- validatedTempFile( "temp file for ProGuard configuration", "jartender_proguard", ".conf", cacheDir )
      cachedJar <- validatedTempFile( "cachedJar file", "jartender_cache_", ".jar", cacheDir )
      configFile <- generateConfigFile( conf, cachedJar, configfilename )
      shrinkerOutput <- executeProguard(configFile)
      validatedOutputJar <- validatedFile( cachedJar, "Proguard did not produce the expected output" )
    } yield validatedOutputJar
  }

  private def executeProguard(configFile: File): FileFailureValidation[Unit] = {
    val cparser = new ConfigurationParser( configFile, props )
    val config = new Configuration
    cparser.parse( config )
    val p = new ProGuard( config )
    p.execute
    Validation.success()
  }

  def generateConfigFileContents( cache: CacheSystem, c: JartenderCacheParameters, cachedJarLocation: File ) = {
    // input jars
    // output jar
    // classfiles (as library jars)
    // library jars
    // defaults (fixed text)
    // proguard_additions.conf
    // keep every entrypoint from user's classfiles
    def quote( s: String ) = "\"" + s + "\""

    val inputjars = c.inputJars map quote map { s => f"-injars $s(!META-INF/MANIFEST.MF)" }
    val outputjar = Array( "-outjars " + quote( cachedJarLocation.getPath ) )
    val classfiles = c.classFiles map quote map { s => f"-injars $s" }
    val libraryjars = c.libraryJars map quote map { s => f"-libraryjars $s" }
    val proguardAdditionsFile = Array( "# Inserting proguard additions file here", fileContentsOrExceptionMessage( new File( c.proguardAdditionsFile ) ) )
    val builtinOptions = Array( c.proguardDefaults )

    keepOptionsForClassfiles( cache, c ) map { keepOptions =>
      val combined = inputjars ++ outputjar ++ classfiles ++ libraryjars ++ builtinOptions ++ proguardAdditionsFile ++ keepOptions

      combined.mkString( "\n" )
    }
  }

  def generateConfigFile( c: JartenderCacheParameters, cachedJarLocation: File, outputLocation: File ) = {
    generateConfigFileContents( cacheSystem, c, cachedJarLocation ) map { contents =>
      Files.write( contents, outputLocation, Charsets.UTF_8 )
      outputLocation
    }
  }

  def keepOptionsForClassfiles( cache: CacheSystem, p: JartenderCacheParameters ) =
    cache.elementsFromClassfiles( p ) map {
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
