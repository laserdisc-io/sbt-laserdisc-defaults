package laserdisc.sbt

import sbt.{Def, Keys, MessageOnlyException}

/** A means of breaking the various defaults into logical groups but keeping the structure consistent
  */
trait DefaultsCategory {

  def fail(msg: String): Nothing = throw new MessageOnlyException(msg)

  def fail(msg: String, cause: Throwable): Nothing = throw new MessageOnlyException(
    s"$msg [${cause.getClass.getSimpleName}:${cause.getMessage}]"
  )

  protected[this] val logger = Keys.sLog

  /** @return build level settings to be applied
    */
  def buildSettings: Seq[Def.Setting[?]] = Seq()

  /** @return project level settings to be applied
    */
  def projectSettings: Seq[Def.Setting[?]] = Seq()
}
