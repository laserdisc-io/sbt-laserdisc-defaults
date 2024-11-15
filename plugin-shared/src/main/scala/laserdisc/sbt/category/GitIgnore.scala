package laserdisc.sbt.category

import laserdisc.sbt.*
import laserdisc.sbt.io.*
import sbt.*

object GitIgnore {
  private val EnabledDefault: Boolean = true
  val EnabledKeyDesc                  = s"Enable .gitconfig generation (default:$EnabledDefault)"
}

/** Auto-templates the .gitignore file
  * @param enabledKey The SBT key to control enable/disabling of this feature
  */
case class GitIgnore(
    enabledKey: SettingKey[Boolean]
)(implicit val ctx: PluginContext)
    extends FileTemplater
    with DefaultsCategory {

  import laserdisc.sbt.category.GitIgnore.*

  override def buildSettings: Seq[Def.Setting[?]] = Seq(
    enabledKey := getSystemPropBoolean(enabledKey, default = EnabledDefault, log.value),
    generateSettings
  )

  override def enabledFlag: SettingKey[Boolean] = enabledKey

  override def inputResourceName: String = "templates/.gitignore"

  override def outputFile = file(".gitignore")
}
