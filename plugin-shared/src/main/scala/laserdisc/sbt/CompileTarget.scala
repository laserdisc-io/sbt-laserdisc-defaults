package laserdisc.sbt

/** This enumeration allows the plugin user to specify whether their project
  * will compile for Scala 2, Scala 3 (latest), Scala 3 LTS, or cross-compile for a combination thereof.
  */
sealed abstract class CompileTarget(val defaultScalaVersion: String, val crossVersions: Seq[String])
object CompileTarget {

  val Scala2Version: String    = "2.13.18"
  val Scala3Version: String    = "3.8.4"
  val Scala3LTSVersion: String = "3.3.8"

  case object Scala2Only extends CompileTarget(Scala2Version, List(Scala2Version))

  case object Scala3Only extends CompileTarget(Scala3Version, List(Scala3Version))

  case object Scala3LTSOnly extends CompileTarget(Scala3LTSVersion, List(Scala3LTSVersion))

  case object Scala2And3 extends CompileTarget(Scala3Version, List(Scala2Version, Scala3Version))

  case object Scala2And3LTS extends CompileTarget(Scala3Version, List(Scala2Version, Scala3LTSVersion))

}
