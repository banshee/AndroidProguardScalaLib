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
import com.restphone.androidproguardscala.RichFile._
import com.restphone.jartender.JavaIdentifier
import com.restphone.jartender.FileFailureValidation._

sealed abstract class CacheResponse {
  def c: CacheEntry
}
case class ExistingLibrary( p: JartenderCacheParameters, c: CacheEntry ) extends CacheResponse
case class BuiltLibrary( p: JartenderCacheParameters, c: CacheEntry ) extends CacheResponse

class CacheSystem {
  def execute( conf: JartenderCacheParameters ): FileFailureValidation[CacheResponse] = {
    // A success on findInCache just means that we didn't throw an
    // exception - it still could be a miss.  Missing is fine; we can continue.
    // An exception isn't fine; we need to stop and report the exception.
    findInCache( conf ) match {
      case Failure( x ) => Failure( x ) // two different failure types
      case Success( Some( x ) ) => x.success
      case Success( None ) => createMissingEntry( conf )
    }
  }

  def createMissingEntry( conf: JartenderCacheParameters ): FileFailureValidation[BuiltLibrary] = {
    for {
      cacheDir <- validDirectory( new File( conf.cacheDir ), "cache directory" )
      configfilename <- validatedTempFile( "temp file for configuration", "jartender_proguard", ".conf", cacheDir )
      cachedJar <- validatedTempFile( "cachedJar file", "jartender_cache_", ".jar", cacheDir )
      configFile <- ProguardConfigFileGenerator.generateConfigFile( this, conf, cachedJar, configfilename )
      proguardOutput <- new ProguardRunner( configfilename ).execute
      newCacheEntry <- cacheEntryForProcessedLibrary( conf, new File( conf.outputJar ) )
      installedCacheEntry <- addCacheEntry( newCacheEntry )
      installedOutputJar <- installOutputJar( cachedJar, conf )
    } yield BuiltLibrary( conf, newCacheEntry )
  }

  def installOutputJar( cachedJar: File, conf: JartenderCacheParameters ): FileFailureValidation[String] =
    convertIoExceptionToValidation( "installing output jar" ) {
      if ( !Files.equal( cachedJar, new File( conf.outputJar ) ) ) {
        Files.copy( cachedJar, new File( conf.outputJar ) )
      }
      "installed jar".success
    }

  def findInCache( p: JartenderCacheParameters ): FileFailureValidation[Option[CacheResponse]] =
    buildUsersAndProviders( p ) map { x =>
      val relevantDependencies = DependencyAnalyser.buildMatchingDependencies( x.providers, x.users )
      val cacheEntry = currentCache.findInCache( relevantDependencies, x.providerFiles )
      cacheEntry map { ExistingLibrary( p, _ ) }
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

  def providerFiles( p: JartenderCacheParameters ) = ProviderFilesInformation.createFromFiles( p.inputJars map stringToFile )

  def addCacheEntry( c: CacheEntry ): FileFailureValidation[Cache] = {
    currentCache = new Cache( currentCache.entries + c )
    currentCache.success
  }

  def cacheEntryForProcessedLibrary( p: JartenderCacheParameters, f: File ): FileFailureValidation[CacheEntry] =
    buildUsersAndProviders( p ) map { x => CacheEntry( usesItems = x.users, providerFileInformation = x.providerFiles, jarfilepath = f.getPath ) }

  def elementsFromClassfiles( p: JartenderCacheParameters ) = elementsFromFiles( jvmFilesInDirectories( p.classFiles ) )
  def elementsFromInputFiles( p: JartenderCacheParameters ) = elementsFromFiles( jvmFilesInDirectories( p.inputJars ) )

  def jvmFilesInDirectories( classfiledirectories: Traversable[String] ) = for {
    classfileDirAsString <- classfiledirectories
    classfiledir = new File( classfileDirAsString )
    IsJvmFile( cf ) <- tree( classfiledir )
  } yield cf

  def elementsFromFiles( fs: Traversable[File] ): FileFailureValidation[List[ClassfileElement]] =
    fs.toList map elementsFromFile suml

  def elementsFromFile( f: File ): FileFailureValidation[List[ClassfileElement]] = {
    DependencyAnalyser.buildItemsFromFile( f ) map { _.elements }
  }

  def classesDefined( fs: Traversable[File] ): FileFailureValidation[List[JavaIdentifier]] =
    elementsFromFiles( fs ) map { _ collect { case ProvidesClass( _, _, internalName, _, _, _ ) => internalName.javaIdentifier } }

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
