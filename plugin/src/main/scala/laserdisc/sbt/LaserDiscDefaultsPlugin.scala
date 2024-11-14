package laserdisc.sbt

import laserdisc.sbt.category.*
import sbt.settingKey

object LaserDiscDefaultsPlugin extends LaserDiscDefaultsPluginBase {

  import laserdisc.sbt.LaserDiscDefaultsPlugin.autoImport.*

  object autoImport {
    lazy val laserdiscFailOnWarn      = settingKey[Boolean](Compiler.FailOnWarnKeyDesc)
    lazy val laserdiscCompileTarget   = settingKey[CompileTarget](Compiler.CompileTargetKeyDesc)
    lazy val laserdiscScalaFmtGenOn   = settingKey[Boolean](ScalaFmt.EnabledKeyDesc)
    lazy val laserdiscGitIgnoreGenOn  = settingKey[Boolean](GitIgnore.EnabledKeyDesc)
    lazy val laserdiscSBTVersionGenOn = settingKey[Boolean](SbtVersion.EnabledKeyDesc)
    lazy val laserdiscRepoName        = settingKey[String](Publishing.RepoNameKeyDesc)
    lazy val laserdiscPublishDefaults = settingKey[PublishDefaults](Publishing.DefaultsKeyDesc)
  }

  private val laserdiscDefaults = new GithubPublishDefaults {
    override def githubOrg: String          = "laserdisc-io"
    override def orgName: String            = "LaserDisc"
    override def groupId: String            = "io.laserdisc"
    override def licenseCheck: LicenseCheck = LicenseRequired
  }

  implicit override val pluginCtx: PluginContext = PluginContext(
    pluginName = PluginBuildInfo.name,
    pluginVersion = PluginBuildInfo.version,
    pluginHomepage = "https://github.com/laserdisc-io/sbt-laserdisc-defaults"
  )

  override val categories: Seq[DefaultsCategory] = Seq(
    Publishing(laserdiscDefaults, laserdiscPublishDefaults, laserdiscRepoName),
    ScalaFmt(laserdiscScalaFmtGenOn),
    GitIgnore(laserdiscGitIgnoreGenOn),
    Compiler(laserdiscFailOnWarn, laserdiscCompileTarget),
    Standards(),
    SbtVersion(laserdiscSBTVersionGenOn),
    Core() // keep last, so the warning message about defaults being used shows first
  )

}
