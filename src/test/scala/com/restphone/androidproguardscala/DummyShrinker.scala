package com.restphone.androidproguardscala

import java.io.File

import com.google.common.base.Charsets
import com.google.common.io.Files
import com.restphone.jartender.FileFailureValidation.FileFailureValidation
import com.restphone.jartender.FileFailureValidation.convertIoExceptionToValidation
import com.restphone.jartender.FileFailureValidation.validDirectory
import com.restphone.jartender.FileFailureValidation.validatedTempFile
import com.restphone.jartender.JartenderCacheParameters
import com.restphone.jartender.Shrinker

class DummyShrinker( val jartenderCacheParameters: JartenderCacheParameters ) extends Shrinker {
  def execute(): FileFailureValidation[File] = convertIoExceptionToValidation( "dummy output file" ) {
    for {
      dir <- validDirectory( new File( jartenderCacheParameters.cacheDir ), "temp directory for dummy shrinker" )
      tempfile <- validatedTempFile( "dummy shrinker temp file", "prefix", "suffix", dir )
    } yield {
      // We just need to create a file of nonzero lenth
      Files.append( "snark", tempfile, Charsets.UTF_8 )
      tempfile.deleteOnExit
      tempfile
    }
  }
}