package laserdisc.sbt.category

import laserdisc.sbt.*
import laserdisc.sbt.defaults.PluginInfo
import org.scalafmt.sbt.ScalafmtPlugin.autoImport.{scalafmtAll, scalafmtCheckAll, scalafmtSbt, scalafmtSbtCheck}
import sbt.Keys.*
import sbt.{Def, *}

object Core extends DefaultsCategory {

  // note how these aliases use `+` on the build/release tasks for cross compilation
  override def projectSettings: Seq[Def.Setting[State => State]] =
    addCommandAlias("format", s";${scalafmtAll.key.label};${scalafmtSbt.key.label}") ++
      addCommandAlias("checkFormat", s";${scalafmtCheckAll.key.label};${scalafmtSbtCheck.key.label}") ++
      addCommandAlias("build", s";checkFormat; ${clean.key.label}; +${test.key.label}") ++
      addCommandAlias("release", s";build; +${publish.key.label}")

  override def buildSettings: Seq[Def.Setting[?]] = Seq(
    // this setting key doesn't do anything, but is simply a hook to display a message when SBT loads the project
    SettingKey[Unit]("laserdiscDefaultsCoreInfo") := {
      val msg  = s"${PluginInfo.name} plugin (v${PluginInfo.version}) is applying defaults"
      val line = "-" * msg.length
      logger.value.pluginWarn(s"\n$line\n$msg\n$line\n")
    }
  )

}
