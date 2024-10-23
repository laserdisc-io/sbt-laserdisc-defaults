package laserdisc.sbt.category

import laserdisc.sbt.*
import laserdisc.sbt.io.*
import sbt.*

case class GitIgnore(enabledKey: SettingKey[Boolean]) extends DefaultsCategory with FileTemplater {

  override def buildSettings: Seq[Def.Setting[?]] = Seq(
    enabledKey := getSystemPropBoolean(enabledKey, default = true, log.value),
    generateSettings
  )

  override def enabledFlag: SettingKey[Boolean] = enabledKey

  override def inputResourceName: String = "templates/.gitignore"

  override def outputFile = file(".gitignore")
}
