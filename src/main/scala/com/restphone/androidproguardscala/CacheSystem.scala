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
import scalaz._
import Scalaz._
import com.restphone.jartender.ClassfileElement
import com.restphone.jartender.FileFailureValidation._

sealed abstract class CacheResponse {
  def f: File
}
case class ExistingLibrary( p: JartenderCacheParameters, f: File ) extends CacheResponse
case class BuiltLibrary( p: JartenderCacheParameters, f: File ) extends CacheResponse

class CacheSystem {
  def findInCache( p: JartenderCacheParameters ) =
    buildUsersAndProviders( p ) map { x =>
      val relevantDependencies = DependencyAnalyser.buildMatchingDependencies( x.providers, x.users )
      currentCache.findInCache( relevantDependencies, x.providerFiles )
    }

  case class UsersAndProviders( providers: Set[ProvidesElement], users: Set[UsesElement], providerFiles: ProviderFilesInformation )

  def buildUsersAndProviders( p: JartenderCacheParameters ) = {
    ( elementsFromClassfiles( p )
      |@| elementsFromInputFiles( p )
      |@| providerFiles( p ) ) {
        ( elementsUsed, elementsProvided, providerFiles ) =>
          val providers = elementsProvided collect { case x: ProvidesElement => x }
          val users = elementsUsed collect { case x: UsesElement => x }
          val relevantDependencies = DependencyAnalyser.buildMatchingDependencies( providers.toSet, users.toSet )
          UsersAndProviders( providers = providers.toSet, users = users.toSet, providerFiles = providerFiles )
      }
  }

  def providerFiles( p: JartenderCacheParameters ) = ProviderFilesInformation.createFromFiles( p.inputJars map { new File( _ ) } )

  def addCacheEntry( c: CacheEntry ) = {
    currentCache = new Cache( currentCache.entries + c )
  }

  def cacheEntryForProcessedLibrary( p: JartenderCacheParameters, f: File ) =
    buildUsersAndProviders( p ) map { x => CacheEntry( usesItems = x.users, providerFileInformation = x.providerFiles, jarfilepath = f.getPath ) }

  def elementsFromClassfiles( p: JartenderCacheParameters ) = elementsFromFiles( jvmFilesInDirectories( p.classFiles ) )
  def elementsFromInputFiles( p: JartenderCacheParameters ) = elementsFromFiles( jvmFilesInDirectories( p.inputJars ) )

  def jvmFilesInDirectories( classfiledirectories: Traversable[String] ) = for {
    classfileDirAsString <- classfiledirectories
    classfiledir = new File( classfileDirAsString )
    IsJvmFile( cf ) <- tree( classfiledir )
  } yield cf

  def elementsFromFiles( fs: Traversable[File] ) = fs.toList map elementsFromFile suml

  def elementsFromFile( f: File ): FileFailureValidation[List[ClassfileElement]] = {
    DependencyAnalyser.buildItemsFromFile( f ) map { _.elements }
  }

  def classesDefined( fs: Traversable[File] ) = elementsFromFiles( fs ) map { _ collect { case ProvidesClass( _, _, internalName, _, _, _ ) => internalName.javaIdentifier } }

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
      case h :: t if jvmExtensions.contains( h.toLowerCase ) && f.isFile => f
    }
  }

  private var currentCache: Cache = new Cache( Set() )
}
