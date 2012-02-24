package com.restphone.androidproguardscala
import scala.util.control.Exception._

object NotNull {
  val catchNull = catching(classOf[NullPointerException])

  def apply[T](x: => T, msg: String = "must not be null"): Option[T] = {
    val result = catchNull.opt(x)
    result match {
      case None | Some(null) => throw new RuntimeException(msg)
      case x => x
    }
  }
}

