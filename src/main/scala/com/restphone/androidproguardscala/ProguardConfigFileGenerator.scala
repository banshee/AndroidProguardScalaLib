package com.restphone.androidproguardscala

import com.google.common.io.Files
import java.io.File
import java.nio.charset.Charset
import com.google.common.base.Charsets
import scala.util.control.Exception._
import java.io.IOException

object ProguardConfigFileGenerator {
  def tree( root: File, descendCheck: File => Boolean = { _ => true } ): Stream[File] = {
    require( root != null )
    def doDirectoryEntries( entries: List[File] ): Stream[File] = {
      entries match {
        case h :: t => tree( h, descendCheck ) ++ doDirectoryEntries( t )
        case Nil => Stream.Empty
      }
    }
    ( root.exists, root.isDirectory, descendCheck( root ) ) match {
      case ( false, _, _ ) => Stream.Empty
      case ( true, true, true ) => root #:: doDirectoryEntries( root.list.toList map { new File( root, _ ) } )
      case ( true, _, _ ) => Stream( root )
    }
  }

  def treeIgnoringHiddenFilesAndDirectories( root: File ) = tree( root, { !_.isHidden } ) filter { !_.isHidden }

  //    if ( !root.exists || ( skipHidden && root.isHidden ) ) Stream.empty
  //    else root #:: (
  //      root.listFiles match {
  //        case null => Stream.empty
  //        case files => files.toStream.flatMap( tree( _, skipHidden ) )
  //      } )

  def fileContentsOrExceptionMessage( f: File ) = {
    try {
      Files.toString( f, Charsets.UTF_8 )
    } catch {
      case e: IOException => f"# ${e.getLocalizedMessage}".replace( '\n', ' ' )
    }
  }
  def generateConfigFileContents( c: ProguardCacheParameters ) = {
    // input jars
    // output jar
    // classfiles (as library jars)
    // library jars
    // defaults (fixed text)
    // proguard_additions.conf
    // keep every entrypoint from user's classfiles
    def quote( s: String ) = "\"" + s + "\""

    val inputjars = c.inputJars map quote map { s => f"-injars $s(!META-INF/MANIFEST.MF)" }
    val outputjar = Array( "-outjars " + quote( c.outputJar ) )
    val classfiles = c.classFiles map quote map { s => f"-injars $s" }
    val libraryjars = c.libraryJars map quote map { s => f"-libraryjars $s" }
    val proguardAdditionsFile = Array( "# Inserting proguard additions file here", fileContentsOrExceptionMessage( new File( c.proguardAdditionsFile ) ) )
    val builtinOptions = Array( c.proguardDefaults )

    val combined = inputjars ++ outputjar ++ classfiles ++ libraryjars ++ builtinOptions ++ proguardAdditionsFile

    combined.mkString( "\n" )
  }

  def keepUserCode( c: ProguardCacheParameters ) = {
    List( "" )
  }
}

//# scala-library.jar was calculated from the classpath
//-injars "C:\Users\james\eclipse\configuration\org.eclipse.osgi\bundles\1103\1\.cp\lib\scala-library.jar"(!META-INF/MANIFEST.MF)
//
//# The CKSUM string is significant - it will be replaced with an actual checksum
//-outjar "C:/cygwin/home/james/workspace/BansheeAndroid/proguard_cache/scala-library.CKSUM.jar"
//-injar "C:/cygwin/home/james/workspace/BansheeAndroid/bin/classes"
//
//# Library jars
//-libraryjars "/BansheeApi/lib/guice-3.0-no_aop.jar"
//-libraryjars "/BansheeApi/lib/javax.inject-1.jar"
//-libraryjars "/BansheeApi/lib/protobuf-java-2.4.1.jar"
//-libraryjars "/BansheeApi/lib/jsr305.jar"
//-libraryjars "/BansheeApi/lib/guice-assistedinject-3.0.jar"
//-libraryjars "/BansheeApi/lib/guava-10.0.1.jar"
//-libraryjars "C:/cygwin/home/james/lib/android-sdk-windows/platforms/android-13/android.jar"
//
//
//# Builtin defaults
//-keep public class * extends android.**
//-dontwarn **$$anonfun$*
//-dontwarn
//-dontoptimize
//-dontobfuscate
//-dontskipnonpubliclibraryclasses
//-dontskipnonpubliclibraryclassmembers
//
//-ignorewarnings
//
//-keepattributes Exceptions,InnerClasses,Signature,Deprecated,
//                SourceFile,LineNumberTable,*Annotation*,EnclosingMethod
//
//-keep public class scala.ScalaObject
//-keep public class scala.Function0, scala.Function1
//# Inserting file C:/cygwin/home/james/workspace/BansheeAndroid/proguard_cache_conf/proguard_additions.conf
//
//# Keep all user code
