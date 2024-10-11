package laserdisc.sbt

import com.github.sbt.git.GitVersioning
import laserdisc.sbt.category.*
import laserdisc.sbt.CompileTarget.CompileTarget
import com.typesafe.sbt.packager.archetypes.JavaAppPackaging
import org.scalafmt.sbt.ScalafmtPlugin
import sbt.*

//noinspection TypeAnnotation
object LaserDiscDefaultsPlugin extends AutoPlugin {

  /* These are the plugins we wish users of our plugin to automatically have. You will need to
   * add the appropriate addSbtPlugin to **build.sbt** to do this (as opposed to having them in the
   * standard project/plugins.sbt, which defines the plugins needed to compile _this_ project)*/
  override def requires: Plugins = plugins.JvmPlugin && ScalafmtPlugin && GitVersioning && JavaAppPackaging

  // we're forcing the plugins to be present above, but still, we define them all as requirements
  override def trigger: PluginTrigger = allRequirements

  object autoImport {
    lazy val laserdiscFailOnWarn      = settingKey[Boolean]("Fail the build if any warnings are present. Enabled by default.")
    lazy val laserdiscScalaFmtGenOn   = settingKey[Boolean]("Enable .scalafmt.conf generation. Enabled by default.")
    lazy val laserdiscGitConfigGenOn  = settingKey[Boolean]("Enable .gitconfig generation. Enabled by default.")
    lazy val laserdiscSBTVersionGenOn = settingKey[Boolean]("Enable SBT version generation. Enabled by default.")
    lazy val laserdiscCompileTarget   = settingKey[CompileTarget]("'Scala2Only', 'Scala3Only' (default), or to cross compile, 'Scala2And3'")
  }

  private val DefaultsGroups = Seq(
    ScalaFmt,
    GitIgnore,
    Compiler,
    Standards,
    SbtVersion,
    Core // keep last, so the warning message about defaults being used shows first
  )

  // apply these to the whole build (i.e. `ThisBuild / whatever..`)
  override lazy val buildSettings: Seq[Def.Setting[?]] = DefaultsGroups.flatMap(_.buildSettings)

  // apply these settings to every 'project'
  override lazy val projectSettings: Seq[Def.Setting[?]] = DefaultsGroups.flatMap(_.projectSettings)

}
