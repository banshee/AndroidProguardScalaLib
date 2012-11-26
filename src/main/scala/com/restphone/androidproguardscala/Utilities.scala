package com.restphone.androidproguardscala

import scala.util.control.Exception._
import java.io.File
import scala.annotation.tailrec

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
}
