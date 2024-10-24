package laserdisc.sbt.category

import laserdisc.sbt.*
import org.scalafmt.sbt.ScalafmtPlugin.autoImport.*
import sbt.Keys.*
import sbt.{Def, *}

case class Core()(implicit val ctx: PluginContext) extends DefaultsCategory {

  // note how these aliases use `+` on the build/release tasks for cross compilation
  override def projectSettings: Seq[Def.Setting[State => State]] =
    addCommandAlias("format", s";${scalafmtAll.key.label};${scalafmtSbt.key.label}") ++
      addCommandAlias("checkFormat", s";${scalafmtCheckAll.key.label};${scalafmtSbtCheck.key.label}") ++
      addCommandAlias("build", s";checkFormat; ${clean.key.label}; +${test.key.label}") ++
      addCommandAlias("release", s";build; +${publish.key.label}")

  override def buildSettings: Seq[Def.Setting[?]] = Seq(
    // this setting key doesn't do anything, but is simply a hook to display a message when SBT loads the project
    SettingKey[Unit]("laserdiscDefaultsCoreInfo") := {
      val msg  = s"${ctx.pluginName} plugin (v${ctx.pluginVersion}) is applying defaults"
      val line = "-" * msg.length
      logger.value.pluginWarn(s"\n$line\n$msg\n$line\n")
    }
  )

}
