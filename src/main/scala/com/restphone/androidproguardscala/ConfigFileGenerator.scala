package com.restphone.androidproguardscala

import com.restphone.jartender.FileFailureValidation._
import java.io.File
import com.google.common.io.Files
import com.google.common.base.Charsets

case class ConfigFileGeneratorResult( conf: JartenderCacheParameters )

trait ConfigFileGenerator {
  def generateConfigFileContents( cache: CacheSystem, c: JartenderCacheParameters, cachedJarLocation: File ): FileFailureValidation[String]

  def generateConfigFile( cache: CacheSystem, c: JartenderCacheParameters, cachedJarLocation: File, f: File ): FileFailureValidation[File] = {
    generateConfigFileContents( cache, c, cachedJarLocation ) map { contents =>
      Files.write( contents, f, Charsets.UTF_8 )
      f
    }
  }
}