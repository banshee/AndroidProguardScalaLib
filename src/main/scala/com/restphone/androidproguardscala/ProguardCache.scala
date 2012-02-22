package com.restphone.androidproguardscala

import com.restphone.androidproguardscala.jruby.JrubyEnvironmentSetup._
import com.restphone.androidproguardscala.jruby.ProguardCacheJava
import org.objectweb.asm.Type

object ProguardCache {
  /**
   * @param directoryContainingJrubyLibraries Full path to the directory contain the jruby libraries
   * @return The cache controller
   */
  def buildCacheController(directoryContainingJrubyLibraries: String) = {
    addJrubyJarfile(pathForJarFileContainingClass(classOf[org.jruby.Ruby]))

    List(
      classOf[org.objectweb.asm.Type],
      classOf[proguard.Initializer],
      classOf[List[String]]) foreach { loadClassIntoJRuby(_) }

    Iterable(
      directoryContainingJrubyLibraries + "lib_src/main/jruby",
      directoryContainingJrubyLibraries + "jruby_lib/main/jruby") map
      addToLoadPath

    new ProguardCacheJava
  }

  private def pathForJarFileContainingClass[T](c: Class[T]) = {
    c.getProtectionDomain.getCodeSource.getLocation.getPath
  }

  private def loadClassIntoJRuby[T](c: Class[T]) = {
    val p = pathForJarFileContainingClass(c)
    loadJarIntoJRuby(p)
  }

  private def loadJarIntoJRuby(path: String) = {
    addJarToLoadPathAndRequire(path)
  }
}