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
    lazy val laserdiscPublishDefaults = settingKey[PublishDefaults]("Publishing Information.") // TODO: docs
    lazy val laserdiscRepoName        = settingKey[String]("Publishing Information.")          // TODO: docs
  }

  implicit val pluginCtx: PluginContext = PluginContext(
    pluginName = PluginBuildInfo.name,
    pluginVersion = PluginBuildInfo.version,
    pluginHomepage = "https://github.com/laserdisc-io/sbt-laserdisc-defaults"
  )

  val LaserDiscPublishingDefaults = new GithubPublishDefaults {
    override def githubOrg: String          = "laserdisc-io"
    override def orgName: String            = "LaserDisc"
    override def groupId: String            = "io.laserdisc"
    override def licenseCheck: LicenseCheck = LicenseRequired
  }

  override val categories: Seq[DefaultsCategory] = Seq(
    Publishing(laserdiscPublishDefaults, laserdiscRepoName, LaserDiscPublishingDefaults),
    ScalaFmt(laserdiscScalaFmtGenOn),
    GitIgnore(laserdiscGitConfigGenOn),
    Compiler(laserdiscFailOnWarn, laserdiscCompileTarget),
    Standards(),
    SbtVersion(laserdiscSBTVersionGenOn),
    Core() // keep last, so the warning message about defaults being used shows first
  )

}
