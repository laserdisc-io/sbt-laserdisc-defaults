package laserdisc.sbt

import com.github.sbt.git.GitVersioning
import com.typesafe.sbt.packager.archetypes.JavaAppPackaging
import org.scalafmt.sbt.ScalafmtPlugin
import sbt.*

/** Base class for concrete plugin implementnations
  */
abstract class LaserDiscDefaultsPluginBase extends AutoPlugin {

  /** Plugin impls should provide information about themselves (e.g. name, version)
    * so that log messages and errors can appropriately be associated with the plugin
    */
  implicit val pluginCtx: PluginContext

  /** Plugin impls can pick and choose [[DefaultsCategory]] implementations
    */
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
