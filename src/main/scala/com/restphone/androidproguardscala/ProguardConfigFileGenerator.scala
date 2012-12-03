package com.restphone.androidproguardscala

import java.io.File
import java.io.IOException

import scala.Array.canBuildFrom

import com.google.common.base.Charsets
import com.google.common.io.Files
import com.restphone.androidproguardscala.RichFile.tree
import com.restphone.jartender.DependencyAnalyser
import com.restphone.jartender.ProvidesClass

object ProguardConfigFileGenerator {
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

    val combined = inputjars ++ outputjar ++ classfiles ++ libraryjars ++ builtinOptions ++ proguardAdditionsFile ++ keepOptionsForClassfiles( cache, c )

    combined.mkString( "\n" )
  }

  def generateConfigFile( cache: CacheSystem, c: JartenderCacheParameters, cachedJarLocation: File, f: File ) = {
    val contents = generateConfigFileContents(cache, c, cachedJarLocation)
    Files.write(contents, f, Charsets.UTF_8)
    f
  }
  
  def keepOptionsForClassfiles( cache: CacheSystem, p: JartenderCacheParameters ) =
    cache.elementsFromClassfiles( p ) collect
      { case ProvidesClass( _, _, internalName, _, _, _ ) => internalName.javaIdentifier } map
      { x => f"-keep class $x {*;}" }

  private def fileContentsOrExceptionMessage( f: File ) = {
    try {
      Files.toString( f, Charsets.UTF_8 )
    } catch {
      case e: IOException => f"# ${e.getLocalizedMessage}".replace( '\n', ' ' )
    }
  }
}
