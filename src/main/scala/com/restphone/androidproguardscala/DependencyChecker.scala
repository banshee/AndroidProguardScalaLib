package com.restphone.androidproguardscala

import java.io.File
import com.restphone.jartender.RichFile.tree
import com.restphone.jartender.DependencyAnalyser
import com.restphone.jartender.ProvidesClass
import com.restphone.jartender.JartenderCacheParameters

class DependencyChecker( c: JartenderCacheParameters ) {
  def classfilesAndJarfilesInDirectories( classfiledirectories: Iterable[String] ) = for {
    classfileDirAsString <- classfiledirectories
    classfiledir = new File( classfileDirAsString )
    cf <- tree( classfiledir ) if ( cf.getName.endsWith( ".class" ) || cf.getName.endsWith( ".jar" ) )
  } yield cf
}