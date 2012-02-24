package com.restphone.androidproguardscala

import org.jruby.Ruby
import org.objectweb.asm.Type

import com.restphone.androidproguardscala.jruby.JrubyEnvironmentSetup.addJarToLoadPathAndRequire
import com.restphone.androidproguardscala.jruby.JrubyEnvironmentSetup.addJrubyJarfile
import com.restphone.androidproguardscala.jruby.JrubyEnvironmentSetup.addToLoadPath
import com.restphone.androidproguardscala.jruby.ProguardCacheJavaInterop

import proguard.Initializer

object ProguardCacheBuilder {
  /**
   * @param directoryContainingJrubyLibraries Full path to the directory containing proguard_lib.rb
   * @return The cache controller
   */
  def buildCacheController(directoryContainingJrubyLibraries: String) = {
    addJrubyJarfile(pathForJarFileContainingClass(classOf[org.jruby.Ruby]))

    Iterable(
      classOf[org.objectweb.asm.Type],
      classOf[proguard.Initializer],
      classOf[List[String]]) foreach { loadClassIntoJRuby(_) }

    Iterable(
      directoryContainingJrubyLibraries,
      directoryContainingJrubyLibraries + "/lib_src/main/jruby",
      directoryContainingJrubyLibraries + "/src/main/jruby",
      directoryContainingJrubyLibraries + "/jruby_lib/main/jruby") foreach
      addToLoadPath

    new ProguardCacheJavaInterop
  }

  private def pathForJarFileContainingClass[T](c: Class[T]) = {
    val calculation = for {
      protectionDomain <- NotNull(c.getProtectionDomain, "failed on getProtectionDomain")
      codeSource <- NotNull(protectionDomain.getCodeSource, "failed on getCodeSource, protection domain is " + protectionDomain)
      location <- NotNull(codeSource.getLocation, "failed on getLocation " + codeSource)
      path <- NotNull(location.getPath, "failed on getPath")
    } yield path
    calculation.get
  }

  private def loadClassIntoJRuby[T](c: Class[T]) = {
    ((pathForJarFileContainingClass[T] _) andThen
      loadJarIntoJRuby)(c)
  }

  private def loadJarIntoJRuby(path: String) = {
    addJarToLoadPathAndRequire(path)
  }
}