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
  def generateConfigFileContents( c: JartenderCacheParameters ) = {
    // input jars
    // output jar
    // classfiles (as library jars)
    // library jars
    // defaults (fixed text)
    // proguard_additions.conf
    // keep every entrypoint from user's classfiles
    def quote( s: String ) = "\"" + s + "\""

    val inputjars = c.inputJars map quote map { s => f"-injars $s(!META-INF/MANIFEST.MF)" }
    val outputjar = Array( "-outjars " + quote( c.outputJar ) )
    val classfiles = c.classFiles map quote map { s => f"-injars $s" }
    val libraryjars = c.libraryJars map quote map { s => f"-libraryjars $s" }
    val proguardAdditionsFile = Array( "# Inserting proguard additions file here", fileContentsOrExceptionMessage( new File( c.proguardAdditionsFile ) ) )
    val builtinOptions = Array( c.proguardDefaults )

    val combined = inputjars ++ outputjar ++ classfiles ++ libraryjars ++ builtinOptions ++ proguardAdditionsFile ++ keepOptionsForClassfiles( c.classFiles )

    combined.mkString( "\n" )
  }

  def classfilesAndJarfilesInDirectories( classfiledirectories: Iterable[String] ) = for {
    classfileDirAsString <- classfiledirectories
    classfiledir = new File( classfileDirAsString )
    cf <- tree( classfiledir ) if ( cf.getName.endsWith( ".class" ) || cf.getName.endsWith( ".jar" ) )
  } yield cf

  def keepOptionsForClassfiles( classfiledirectories: Iterable[String] ) = {
    for {
      file <- classfilesAndJarfilesInDirectories( classfiledirectories )
      klass <- classesDefined( file )
      classname = klass.s
    } yield {
      f"-keep class $classname {*;}"
    }
  }

  def classesDefined( f: File ) = {
    for {
      i <- DependencyAnalyser.buildItemsFromFile( f )
      ProvidesClass( _, _, internalName, _, _, _ ) <- i.elements
    } yield internalName.javaIdentifier
  }

  private def fileContentsOrExceptionMessage( f: File ) = {
    try {
      Files.toString( f, Charsets.UTF_8 )
    } catch {
      case e: IOException => f"# ${e.getLocalizedMessage}".replace( '\n', ' ' )
    }
  }

  def extractClassfileElements( c: JartenderCacheParameters ) = {
    for {
      f <- classfilesAndJarfilesInDirectories( c.classFiles )
      i <- DependencyAnalyser.buildItemsFromFile( f )
      e <- i.elements
    } yield e
  }
}
