package laserdisc.sbt.category

import laserdisc.sbt.LaserDiscDefaultsPlugin.autoImport.laserdiscScalaFmtGenOn
import laserdisc.sbt.io.*
import laserdisc.sbt.*
import sbt.*

object ScalaFmt extends DefaultsCategory with FileTemplater {

  override def projectSettings: Seq[Def.Setting[State => State]] =
    addCommandAlias("format", ";scalafmtAll; scalafmtSbt") ++
      addCommandAlias("checkFormat", ";scalafmtCheckAll; scalafmtCheck")

  override def buildSettings: Seq[Def.Setting[?]] =
    Seq(
      laserdiscScalaFmtGenOn := getSystemPropBoolean(laserdiscScalaFmtGenOn, default = true, log.value),
      generateSettings
    )

  override def enabledFlag: SettingKey[Boolean] = laserdiscScalaFmtGenOn

  override def inputResourceName: String = "templates/.scalafmt.conf"

  override def outputFile = file(".scalafmt.conf")
}
