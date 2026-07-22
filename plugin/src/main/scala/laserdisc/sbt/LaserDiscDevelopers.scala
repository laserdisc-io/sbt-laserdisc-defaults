package laserdisc.sbt

import sbt.{uri, Developer}

/** Convenience so we don't have to repeat all this info in each laserdisc project
  */
object LaserDiscDevelopers {
  val Julien  = Developer("sirocchi", "Julien Sirocchi", "", uri("https://github.com/sirocchj"))
  val Filippo = Developer("barambani", "Filippo Mariotti", "", uri("https://github.com/barambani"))
  val Barry   = Developer("barryoneill", "Barry O'Neill", "", uri("https://github.com/barryoneill"))
  val Dmytro  = Developer("semenodm", "Dmytro Semenov", "", uri("https://github.com/semenodm"))
  val Amir    = Developer("amir", "Amir Saeid", "", uri("https://github.com/amir"))
  val Jenny   = Developer("jennyleahy", "Jenny Leahy", "", uri("https://github.com/jennyleahy"))
}
