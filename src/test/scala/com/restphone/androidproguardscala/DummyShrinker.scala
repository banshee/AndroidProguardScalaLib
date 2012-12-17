package com.restphone.androidproguardscala

import com.restphone.jartender.FileFailureValidation._
import java.io.File
import com.google.common.base.Charsets
import com.google.common.io.Files
import scalaz._
import Scalaz._

class DummyShrinker extends Shrinker {
  def execute( conf: JartenderCacheParameters ): FileFailureValidation[File] = convertIoExceptionToValidation("dummy output file"){
    val f = new File( conf.outputJar )
    println( f"appending tofi f $f" )
    Files.append( "snark", f, Charsets.UTF_8 )
    f.success
  }
}