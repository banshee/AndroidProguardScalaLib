package com.restphone.androidproguardscala

import java.io.File
import java.io.IOException
import scala.PartialFunction._
import scala.util.matching.Regex
import com.google.common.base.Charsets
import com.google.common.io.Files
import com.restphone.androidproguardscala.RichFile.tree
import com.restphone.jartender.DependencyAnalyser
import com.restphone.jartender.ProvidesClass
import com.restphone.jartender.ProvidesElement
import com.restphone.jartender.UsesElement

class CacheSystem {
  private var currentCache: Cache = new Cache( Set() )

  def findInCache( p: JartenderCacheParameters ) = {
    val elementsUsed = elementsFromClassfiles(p)
    val elementsProvided = elementsFromInputFiles(p)
    val providers = elementsProvided collect { case x: ProvidesElement => x }
    val users = elementsUsed collect { case x: UsesElement => x }
    val relevantDependencies = DependencyAnalyser.buildMatchingDependencies( providers.toSet.seq, users.toSet.seq )
    val providerFiles = ProviderFilesInformation( p.inputJars map { new File( _ ) } )
    currentCache.findInCache( relevantDependencies, providerFiles )
  }
  
  def elementsFromClassfiles( p: JartenderCacheParameters ) = elementsFromFiles( jvmFilesInDirectories( p.classFiles ) )
  def elementsFromInputFiles( p: JartenderCacheParameters ) = elementsFromFiles( jvmFilesInDirectories( p.inputJars ) )
  
  def jvmFilesInDirectories( classfiledirectories: Traversable[String] ) = for {
    classfileDirAsString <- classfiledirectories
    classfiledir = new File( classfileDirAsString )
    IsJvmFile( cf ) <- tree( classfiledir )
  } yield cf

  def keepOptionsForClassfiles( classfiledirectories: Iterable[String] ) = {
    for {
      klass <- classesDefined( jvmFilesInDirectories( classfiledirectories ) )
      classname = klass.s
    } yield {
      f"-keep class $classname {*;}"
    }
  }

  def elementsFromFiles( fs: Traversable[File] ) = for {
    f <- fs.par
    e <- elementsFromFile( f )
  } yield e

  def elementsFromFile( f: File ) = for {
    i <- DependencyAnalyser.buildItemsFromFile( f )
    e <- i.elements
  } yield e

  def classesDefined( fs: Traversable[File] ) = elementsFromFiles( fs ) collect { case ProvidesClass( _, _, internalName, _, _, _ ) => internalName.javaIdentifier }

  private def fileContentsOrExceptionMessage( f: File ) = {
    try {
      Files.toString( f, Charsets.UTF_8 )
    } catch {
      case e: IOException => f"# ${e.getLocalizedMessage}".replace( '\n', ' ' )
    }
  }

  val IsJvmFile = new Object {
    val jvmExtensions = Set( "class", "jar" )
    def unapply( f: File ) = condOpt( f.getName.split( '.' ).reverse.toList ) {
      case h :: t if jvmExtensions.contains( h.toLowerCase ) && !f.isDirectory => f
    }
  }
}
