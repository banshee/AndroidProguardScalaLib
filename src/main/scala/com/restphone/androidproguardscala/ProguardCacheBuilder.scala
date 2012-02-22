package com.restphone.androidproguardscala

import com.restphone.androidproguardscala.jruby.JrubyEnvironmentSetup._
import com.restphone.androidproguardscala.jruby.ProguardCache
import org.objectweb.asm.Type
import com.restphone.androidproguardscala.jruby.ProguardCacheJavaInterop

object ProguardCacheBuilder {
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

    new ProguardCacheJavaInterop
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