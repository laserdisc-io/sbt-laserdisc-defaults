package laserdisc.sbt

import sbt.{Developer, URL, url}

case class PublishSettings(
    repoName: String,
    maintainers: List[Developer],
    license: Option[(String, URL)] = None,
    groupID: String = "io.laserdisc",
    orgName: String = "LaserDisc"
)

object LaserdiscMaintainers {
  val Julien  = Developer("sirocchi", "Julien Sirocchi", "", url("https://github.com/sirocchj"))
  val Filippo = Developer("barambani", "Filippo Mariotti", "", url("https://github.com/barambani"))
  val Barry   = Developer("barryoneill", "Barry O'Neill", "", url("https://github.com/barryoneill"))
  val Dmytro  = Developer("semenodm", "Dmytro Semenov", "", url("https://github.com/semenodm"))
  val Amir    = Developer("amir", "Amir Saeid", "", url("https://github.com/amir"))
  val Jenny   = Developer("jennyleahy", "Jenny Leahy", "", url("https://github.com/jennyleahy"))
}
