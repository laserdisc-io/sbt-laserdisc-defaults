package laserdisc.sbt

/** This enumeration allows the plugin user to specify whether their project
  * will compile for Scala 2, Scala 3, or cross-compile for both.
  */
object CompileTarget extends Enumeration {

  val Scala2Version: String = "2.13.15"
  val Scala3Version: String = "3.3.4"

  type CompileTarget = Value

  val Scala2Only, Scala3Only, Scala2And3 = Value

  /** By default, the plugin selects scala 3, developers should hopefully attempt to move directly to 3
    */
  val Default: CompileTarget = Scala3Only

  implicit class CTValue(suit: Value) {

    def defaultScalaVersion: String = suit match {
      case Scala2Only => Scala2Version
      case Scala3Only => Scala3Version
      case Scala2And3 => Scala3Version // cross compiles, but defaults to scala 3 (affects mostly IDE auto-config)
    }

    def crossVersions: Seq[String] = suit match {
      case Scala2Only => List(Scala2Version)
      case Scala3Only => List(Scala3Version)
      case Scala2And3 => List(Scala2Version, Scala3Version)
    }

  }

}
