package com.restphone.androidproguardscala

import java.io.File

import com.restphone.androidproguardscala.RichFile.tree
import com.restphone.jartender.DependencyAnalyser
import com.restphone.jartender.ProvidesClass

class DependencyChecker( c: JartenderCacheParameters ) {
  val classfileElements = {
    val classfilesForUsercode = for {
      file <- classfilesAndJarfilesInDirectories( c.classFiles )
      i <- DependencyAnalyser.buildItemsFromFile( file )
    } yield i
    val classfilesForLibraryJars = for {
      file <- classfilesAndJarfilesInDirectories( c.libraryJars )
      i <- DependencyAnalyser.buildItemsFromFile( file )
    } yield i
  }

  def classfilesAndJarfilesInDirectories( classfiledirectories: Iterable[String] ) = for {
    classfileDirAsString <- classfiledirectories
    classfiledir = new File( classfileDirAsString )
    cf <- tree( classfiledir ) if ( cf.getName.endsWith( ".class" ) || cf.getName.endsWith( ".jar" ) )
  } yield cf
}