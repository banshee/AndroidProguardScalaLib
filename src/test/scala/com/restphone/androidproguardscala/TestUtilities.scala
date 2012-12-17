package com.restphone.androidproguardscala

import org.scalatest.matchers.ShouldMatchers
import org.scalatest.FunSuite
import scala.annotation.tailrec
import scala.util.Random.shuffle
import scalaz._
import Scalaz._
import java.io.File
import com.restphone.androidproguardscala.RichFile._
import com.google.common.io.Files
import com.restphone.jartender.UsesClass
import com.restphone.jartender._
import com.restphone.jartender.FileFailureValidation._
import com.google.common.base.Charsets
import scala.PartialFunction._


object TestUtilities {
  def getResource( s: String ) = {
    val extractFilePathExpr = """file:/(.*)""".r
    val root = Option( Thread.currentThread.getContextClassLoader.getResource( s ) )
    root flatMap { condOpt( _ ) { case extractFilePathExpr( f ) => f } }
  }
}