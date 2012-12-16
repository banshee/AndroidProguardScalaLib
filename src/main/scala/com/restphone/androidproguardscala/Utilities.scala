package com.restphone.androidproguardscala

import java.io.ByteArrayOutputStream
import java.io.File
import java.io.ObjectOutputStream
import scala.annotation.tailrec
import scala.util.control.Exception._
import java.io.ByteArrayInputStream
import java.io.ObjectInputStream
import scalaz._
import Scalaz._

object NotNull {
  val catchNull = catching( classOf[NullPointerException] )

  def apply[T]( x: => T, msg: String = "must not be null" ): Option[T] = {
    catchNull.opt( x ) match {
      case None | Some( null ) => throw new RuntimeException( msg )
      case x => x
    }
  }
}

object RichFile {
  def slurp( f: File ) = {
    val s = scala.io.Source.fromFile( f )
    val result = s.getLines.mkString( "\n" )
    s.close()
    result
  }
  def ensureDirExists( f: File ) =
    if ( !f.exists ) f.mkdir

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

  def splitFile( f: File ): List[File] = {
    @tailrec def splitFileRecursive( f: File, acc: List[File] ): List[File] = {
      f.getParentFile match {
        case null => f :: acc
        case p => splitFileRecursive( p, f :: acc )
      }
    }
    splitFileRecursive( f, List() )
  }
  def joinFile( fs: List[File] ) = fs.mkString( File.pathSeparator )

  def treeIgnoringHiddenFilesAndDirectories( root: File ) = tree( root, { !_.isHidden } ) filter { !_.isHidden }

  def stringToFile( s: String ) = new File( s )
}

object SerializableUtilities {
  def converToByteArray( x: Serializable ) = {
    val out = new ByteArrayOutputStream
    val objout = new ObjectOutputStream( out )
    objout.writeObject( x )
    out.toByteArray
  }

  def byteArrayToObject[T]( bytes: Array[Byte] ): Option[T] = {
    try {
      val input = new ByteArrayInputStream( bytes )
      val readstream = new ObjectInputStream( input )
      some( readstream.readObject.asInstanceOf[T] )
    } catch {
      case _: Throwable => None
    }
  }
}
