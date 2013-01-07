package com.restphone.androidproguardscala

import com.restphone.jartender.FileFailureValidation._
import java.io.File
import com.google.common.io.Files
import com.google.common.base.Charsets
import com.restphone.jartender.JartenderCacheParameters
import com.restphone.jartender.CacheSystem

case class ConfigFileGeneratorResult( conf: JartenderCacheParameters )

trait ConfigFileGenerator {
  def generateConfigFileContents( cache: CacheSystem, c: JartenderCacheParameters, cachedJarLocation: File ): FailureValidation[String]

  def generateConfigFile( cache: CacheSystem, c: JartenderCacheParameters, cachedJarLocation: File, f: File ): FailureValidation[File] = {
    generateConfigFileContents( cache, c, cachedJarLocation ) map { contents =>
      Files.write( contents, f, Charsets.UTF_8 )
      f
    }
  }
}