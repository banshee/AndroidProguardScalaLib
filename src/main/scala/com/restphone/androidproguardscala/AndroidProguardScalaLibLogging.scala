package com.restphone.androidproguardscala

import com.restphone.jartender.ProvidesLogging

object AndroidProguardScalaLibLogging {
  val NullLogger = new ProvidesLogging {
    def logMsg(msg: String) {}
    def logError(msg: String) {}
  }
}
