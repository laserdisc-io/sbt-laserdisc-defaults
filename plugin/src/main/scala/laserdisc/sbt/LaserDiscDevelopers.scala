package laserdisc.sbt

import sbt.{Developer, url}

/** Convenience so we don't have to repeat all this info in each laserdisc project
  */
object LaserDiscDevelopers {
  val Julien  = Developer("sirocchi", "Julien Sirocchi", "", url("https://github.com/sirocchj"))
  val Filippo = Developer("barambani", "Filippo Mariotti", "", url("https://github.com/barambani"))
  val Barry   = Developer("barryoneill", "Barry O'Neill", "", url("https://github.com/barryoneill"))
  val Dmytro  = Developer("semenodm", "Dmytro Semenov", "", url("https://github.com/semenodm"))
  val Amir    = Developer("amir", "Amir Saeid", "", url("https://github.com/amir"))
  val Jenny   = Developer("jennyleahy", "Jenny Leahy", "", url("https://github.com/jennyleahy"))
}
