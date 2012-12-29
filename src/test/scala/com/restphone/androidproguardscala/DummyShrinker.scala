package com.restphone.androidproguardscala

import com.restphone.jartender.FileFailureValidation._
import java.io.File
import com.google.common.base.Charsets
import com.google.common.io.Files
import scalaz._
import Scalaz._
import com.restphone.jartender.JartenderCacheParameters
import com.restphone.jartender.Shrinker

class DummyShrinker extends Shrinker {
  def execute( conf: JartenderCacheParameters ): FileFailureValidation[File] = convertIoExceptionToValidation("dummy output file"){
    val f = new File( conf.outputJar )
    // We just need to create a file of nonzero lenth
    Files.append( "snark", f, Charsets.UTF_8 )
    f.success
  }
}