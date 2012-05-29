package com.restphone.androidproguardscala

import scala.util.control.Exception._
import java.io.File
import org.eclipse.core.runtime.IPath

object NotNull {
  val catchNull = catching(classOf[NullPointerException])

  def apply[T](x: => T, msg: String = "must not be null"): Option[T] = {
    catchNull.opt(x) match {
      case None | Some(null) => throw new RuntimeException(msg)
      case x => x
    }
  }
}

class RichFile(f: File) {
  def /(that: String) = new java.io.File(f, that)
}

object RichFile {
  def slurp(f: File) = {
    val s = scala.io.Source.fromFile(f)
    val result = s.getLines.mkString("\n")
    s.close()
    result
  }
  def ensureDirExists(f: File) =
    if (!f.exists) f.mkdir
}

class RichPath(p: IPath) {
  def /(that: String) = p.append(that)
}

object RichPath {
  implicit def toRichPath(p: IPath): RichPath = new RichPath(p)
//  implicit def convertFileToPath(f: java.io.File): IPath = Path.fromOSString(f.toString)
//  implicit def convertUrlToPath(u: java.net.URL) = {
//    val x = Platform.resolve(u)
//    new Path(x.getFile)
//  }
  def ensureDirExists(p: IPath) = RichFile.ensureDirExists(p.toFile)
}

