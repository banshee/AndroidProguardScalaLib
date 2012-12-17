package com.restphone.androidproguardscala

import com.restphone.jartender.FileFailureValidation._
import java.io.File

trait Shrinker {
  def execute( conf: JartenderCacheParameters ): FileFailureValidation[File]
}