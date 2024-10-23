package laserdisc.sbt.category

import laserdisc.sbt.*
import laserdisc.sbt.io.*
import sbt.*

case class ScalaFmt(enabledKey: SettingKey[Boolean]) extends DefaultsCategory with FileTemplater {

  override def projectSettings: Seq[Def.Setting[State => State]] =
    addCommandAlias("format", ";scalafmtAll; scalafmtSbt") ++
      addCommandAlias("checkFormat", ";scalafmtCheckAll; scalafmtCheck")

  override def buildSettings: Seq[Def.Setting[?]] =
    Seq(
      enabledKey := getSystemPropBoolean(enabledKey, default = true, log.value),
      generateSettings
    )

  override def enabledFlag: SettingKey[Boolean] = enabledKey

  override def inputResourceName: String = "templates/.scalafmt.conf"

  override def outputFile = file(".scalafmt.conf")
}
