package laserdisc.sbt.category

import laserdisc.sbt.*
import laserdisc.sbt.category.ScalaFmt.*
import laserdisc.sbt.io.*
import sbt.*

object ScalaFmt {

  private val EnabledDefault: Boolean = true
  val EnabledKeyDesc                  = s"Enable .scalafmt.conf generation (default:$EnabledDefault)"
}

/** Auto-templates the .scalafmt.conf file
  * @param enabledKey The SBT key to control enable/disabling of this feature
  */
case class ScalaFmt(enabledKey: SettingKey[Boolean])(implicit val ctx: PluginContext) extends FileTemplater with DefaultsCategory {

  override def projectSettings: Seq[Def.Setting[State => State]] =
    addCommandAlias("format", ";scalafmtAll; scalafmtSbt") ++
      addCommandAlias("checkFormat", ";scalafmtCheckAll; scalafmtCheck")

  override def buildSettings: Seq[Def.Setting[?]] =
    Seq(
      enabledKey := getSystemPropBoolean(enabledKey, default = EnabledDefault, log.value),
      generateSettings
    )

  override def enabledFlag: SettingKey[Boolean] = enabledKey

  override def inputResourceName: String = "templates/.scalafmt.conf"

  override def outputFile = file(".scalafmt.conf")
}
