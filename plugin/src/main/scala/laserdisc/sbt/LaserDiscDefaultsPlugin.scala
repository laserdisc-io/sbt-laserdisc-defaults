package laserdisc.sbt

import laserdisc.sbt.category.*
import sbt.settingKey

object LaserDiscDefaultsPlugin extends LaserDiscDefaultsPluginBase {

  import laserdisc.sbt.LaserDiscDefaultsPlugin.autoImport.*

  object autoImport {
    lazy val laserdiscFailOnWarn      = settingKey[Boolean]("Fail the build if any warnings are present. Enabled by default.")
    lazy val laserdiscScalaFmtGenOn   = settingKey[Boolean]("Enable .scalafmt.conf generation. Enabled by default.")
    lazy val laserdiscGitConfigGenOn  = settingKey[Boolean]("Enable .gitconfig generation. Enabled by default.")
    lazy val laserdiscSBTVersionGenOn = settingKey[Boolean]("Enable SBT version generation. Enabled by default.")
    lazy val laserdiscCompileTarget   = settingKey[CompileTarget]("'Scala2Only', 'Scala3Only' (default), or to cross compile, 'Scala2And3'")
    lazy val laserdiscPublishSettings = settingKey[PublishInfo]("Publishing Information.") // TODO: docs
    lazy val laserdiscRepoName        = settingKey[String]("Publishing Information.")      // TODO: docs
  }

  implicit val pluginCtx: PluginContext = PluginContext(
    pluginName = PluginBuildInfo.name,
    pluginVersion = PluginBuildInfo.version,
    pluginHomepage = "https://github.com/laserdisc-io/sbt-laserdisc-defaults"
  )

  private val LaserDiscPublishingDefaults = GithubPublishSettings(
    publishOrgName = "LaserDisc",
    publishGroupId = "io.laserdisc",
    githubOrg = "laserdisc-io"
  )

  override val categories: Seq[DefaultsCategory] = Seq(
    Publishing(laserdiscPublishSettings, laserdiscRepoName, LaserDiscPublishingDefaults),
    ScalaFmt(laserdiscScalaFmtGenOn),
    GitIgnore(laserdiscGitConfigGenOn),
    Compiler(laserdiscFailOnWarn, laserdiscCompileTarget),
    Standards(),
    SbtVersion(laserdiscSBTVersionGenOn),
    Core() // keep last, so the warning message about defaults being used shows first
  )

}
