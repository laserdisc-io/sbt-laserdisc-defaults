package laserdisc.sbt

import com.github.sbt.git.GitVersioning
import com.typesafe.sbt.packager.archetypes.JavaAppPackaging
import laserdisc.sbt.category.*
import org.scalafmt.sbt.ScalafmtPlugin
import sbt.*

object LaserDiscDefaultsPlugin extends LaserDiscDefaultsPluginImpl {

  import laserdisc.sbt.LaserDiscDefaultsPlugin.autoImport.*

  object autoImport {
    lazy val laserdiscFailOnWarn      = settingKey[Boolean]("Fail the build if any warnings are present. Enabled by default.")
    lazy val laserdiscScalaFmtGenOn   = settingKey[Boolean]("Enable .scalafmt.conf generation. Enabled by default.")
    lazy val laserdiscGitConfigGenOn  = settingKey[Boolean]("Enable .gitconfig generation. Enabled by default.")
    lazy val laserdiscSBTVersionGenOn = settingKey[Boolean]("Enable SBT version generation. Enabled by default.")
    lazy val laserdiscCompileTarget   = settingKey[CompileTarget]("'Scala2Only', 'Scala3Only' (default), or to cross compile, 'Scala2And3'")
    lazy val laserdiscPublishSettings = settingKey[Option[PublishSettings]](
      "If provided, configure the publishing defaults for laserdisc projects with these settings"
    )
  }

  override val categories: Seq[DefaultsCategory] = Seq(
    LaserdiscPublishDefaults(laserdiscPublishSettings),
    ScalaFmt(laserdiscScalaFmtGenOn),
    GitIgnore(laserdiscGitConfigGenOn),
    Compiler(laserdiscFailOnWarn, laserdiscCompileTarget),
    Standards,
    SbtVersion(laserdiscSBTVersionGenOn),
    Core // keep last, so the warning message about defaults being used shows first
  )

}

abstract class LaserDiscDefaultsPluginImpl extends AutoPlugin {

  val categories: Seq[DefaultsCategory]

  /* These are the plugins we wish users of our plugin to automatically have. You will need to
   * add the appropriate addSbtPlugin to **build.sbt** to do this (as opposed to having them in the
   * standard project/plugins.sbt, which defines the plugins needed to compile _this_ project)*/
  override def requires: Plugins = plugins.JvmPlugin && ScalafmtPlugin && GitVersioning && JavaAppPackaging

  // we're forcing the plugins to be present above, but still, we define them all as requirements
  override def trigger: PluginTrigger = allRequirements

  // apply these to the whole build (i.e. `ThisBuild / whatever..`)
  override lazy val buildSettings: Seq[Def.Setting[?]] = categories.flatMap(_.buildSettings)

  // apply these settings to every 'project'
  override lazy val projectSettings: Seq[Def.Setting[?]] = categories.flatMap(_.projectSettings)

}
