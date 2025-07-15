package laserdisc.sbt

/** This enumeration allows the plugin user to specify whether their project
  * will compile for Scala 2, Scala 3, or cross-compile for both.
  */
sealed abstract class CompileTarget(val defaultScalaVersion: String, val crossVersions: Seq[String])
object CompileTarget {

  val Scala2Version: String = "2.13.16"
  val Scala3Version: String = "3.3.6"

  final case object Scala2Only extends CompileTarget(Scala2Version, List(Scala2Version))

  final case object Scala3Only extends CompileTarget(Scala3Version, List(Scala3Version))

  // cross compiles, but defaults to scala 3 (affects mostly IDE auto-config)
  final case object Scala2And3 extends CompileTarget(Scala3Version, List(Scala2Version, Scala3Version))

}
