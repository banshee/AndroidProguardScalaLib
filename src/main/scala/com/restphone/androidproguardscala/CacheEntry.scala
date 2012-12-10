package com.restphone.androidproguardscala

import java.io.File
import java.security.MessageDigest
import scala.Array.canBuildFrom
import com.google.common.io.Files
import com.restphone.jartender.UsesElement
import scalaz._
import Scalaz._
import scalaz.ValidationNEL
import com.restphone.jartender.FileFailureValidation._

case class Cache( entries: Set[CacheEntry] ) {
  private val pentries = entries.par
  def findInCache( items: Set[UsesElement], providers: ProviderFilesInformation ): Option[CacheEntry] =
    pentries.find { _.thisCacheEntryProvides( items, providers ) }
}

case class CacheEntry(
  usesItems: Set[UsesElement],
  providerFileInformation: ProviderFilesInformation,
  jarfilepath: String ) {
  def thisCacheEntryProvides( items: Set[UsesElement], providers: ProviderFilesInformation ) = {
    items.subsetOf( usesItems ) && ( providers == providerFileInformation )
  }
}
case class ProviderFileInformation( filename: String, checksum: String )
case class ProviderFilesInformation( items: Set[ProviderFileInformation] )

object ProviderFilesInformation {
  def createFromFiles( files: Traversable[File] ): FileFailureValidation[ProviderFilesInformation] = {
    def buildProviderFileInformationOrFailure( f: File ) =
      convertIoExceptionToValidation( f ) { List( ProviderFileInformation( f.getPath, fileChecksum( f ) ) ).success }
    val pfiItems = ( files.par map buildProviderFileInformationOrFailure ).toList
    pfiItems.suml map { items => ProviderFilesInformation( items.toSet ) }
  }

  def createFromParameters( p: JartenderCacheParameters ): FileFailureValidation[ProviderFilesInformation] = createFromFiles( p.inputJars map { new File( _ ) } )

  private val messageDigest = MessageDigest.getInstance( "SHA-512" )

  def fileChecksum( f: File ): String = {
    val contents = Files.toByteArray( f )
    val checksum = messageDigest.digest( contents )
    javax.xml.bind.DatatypeConverter.printBase64Binary( checksum );
  }
}
