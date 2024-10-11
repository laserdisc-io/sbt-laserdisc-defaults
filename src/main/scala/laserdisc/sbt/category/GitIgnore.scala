package laserdisc.sbt.category

import laserdisc.sbt.*
import laserdisc.sbt.LaserDiscDefaultsPlugin.autoImport.laserdiscGitConfigGenOn
import laserdisc.sbt.io.*
import sbt.*

object GitIgnore extends DefaultsCategory with FileTemplater {

  override def buildSettings: Seq[Def.Setting[?]] = Seq(
    laserdiscGitConfigGenOn := getSystemPropBoolean(laserdiscGitConfigGenOn, default = true, log.value),
    generateSettings
  )

  override def enabledFlag: SettingKey[Boolean] = laserdiscGitConfigGenOn

  override def inputResourceName: String = "templates/.gitignore"

  override def outputFile = file(".gitignore")
}
