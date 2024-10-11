package laserdisc.sbt

import sbt.{Def, Keys}

/** A means of breaking the various defaults into logical groups but keeping the structure consistent
  */
trait DefaultsCategory {

  protected[this] val logger = Keys.sLog

  /** @return build level settings to be applied
    */
  def buildSettings: Seq[Def.Setting[?]] = Seq()

  /** @return project level settings to be applied
    */
  def projectSettings: Seq[Def.Setting[?]] = Seq()
}
