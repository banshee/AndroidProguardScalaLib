package com.restphone.androidproguardscala

import com.restphone.jartender.UsesElement
import java.io.File
import java.security.MessageDigest
import com.google.common.io.Files
import com.google.common.io.ByteStreams
import scala.actors.Futures.future

case class Cache( entries: Set[CacheEntry] ) {
  private val pentries = entries.par
  def findInCache( items: Set[UsesElement], providers: ProviderFilesInformation ) =
    pentries.find { _.thisCacheEntryProvides( items, providers ) }

  def findInCache( c: JartenderCacheParameters ) = {
  }
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
  def apply( files: Traversable[File] ): ProviderFilesInformation = {
    val items = files.par map { f => ProviderFileInformation( f.getPath, fileChecksum( f ) ) }
    new ProviderFilesInformation( items.seq.toSet )
  }

  private val messageDigest = MessageDigest.getInstance( "SHA-512" )

  def fileChecksum( f: File ) = {
    val contents = Files.toByteArray( f )
    javax.xml.bind.DatatypeConverter.printBase64Binary( contents );
  }
}
