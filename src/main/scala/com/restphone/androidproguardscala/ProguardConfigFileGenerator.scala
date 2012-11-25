package com.restphone.androidproguardscala

import java.io.File
import java.io.IOException

import scala.Array.canBuildFrom
import scala.Option.option2Iterable
import scala.annotation.tailrec
import scala.collection.immutable.Stream.consWrapper

import com.google.common.base.Charsets
import com.google.common.io.Files
import com.restphone.jartender.DependencyAnalyser
import com.restphone.jartender.ProvidesClass

object ProguardConfigFileGenerator {
  def tree( root: File, descendCheck: File => Boolean = { _ => true } ): Stream[File] = {
    require( root != null )
    def directoryEntries( f: File ) = for {
      direntries <- Option( f.list ).toStream
      d <- direntries
    } yield new File( f, d )
    val shouldDescend = root.isDirectory && descendCheck( root )
    ( root.exists, shouldDescend ) match {
      case ( false, _ ) => Stream.Empty
      case ( true, true ) => root #:: ( directoryEntries( root ) flatMap { tree( _, descendCheck ) } )
      case ( true, false ) => Stream( root )
    }
  }
  
  def splitFile(f: File) : List[File] = {
    @tailrec def splitFileRecursive(f: File, acc: List[File]) : List[File] = {
      f.getParentFile match {
        case null => f :: acc
        case p => splitFileRecursive(p, f :: acc)
      }
    }
    splitFileRecursive(f, List())
  }

  def treeIgnoringHiddenFilesAndDirectories( root: File ) = tree( root, { !_.isHidden } ) filter { !_.isHidden }

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

    val combined = inputjars ++ outputjar ++ classfiles ++ libraryjars ++ builtinOptions ++ proguardAdditionsFile ++ keepOptionsForClassfiles( c.classFiles )

    combined.mkString( "\n" )
  }

  def classfilesAndJarfilesInDirectories( classfiledirectories: Iterable[String] ) = for {
    classfileDirAsString <- classfiledirectories
    classfiledir = new File( classfileDirAsString )
    cf <- tree( classfiledir ) if ( cf.getName.endsWith( ".class" ) || cf.getName.endsWith( ".jar" ) )
  } yield cf

  def keepOptionsForClassfiles( classfiledirectories: Iterable[String] ) = {
    for {
      file <- classfilesAndJarfilesInDirectories( classfiledirectories )
      klass <- classesDefined(file)
      classname = klass.s
    } yield {
      f"-keep class $classname {*;}"
    }
  }

  import com.restphone.jartender.DependencyAnalyser

  def classesDefined( f: File ) = {
    val items = DependencyAnalyser.buildItemsFromFile(f)
    for {
      i <- items
      ProvidesClass(_, _, internalName, _, _, _) <- i.elements
    } yield internalName.javaIdentifier
  }
}
//
//
//  version: Int,
//  access: Int,
//  internalName: InternalName,
//  signature: Option[Signature],
//  superName: InternalName,
//  interfaces: List[InternalName]
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
