package laserdisc.sbt.category

import com.github.sbt.git.ReadableGit
import com.github.sbt.git.SbtGit.GitKeys.gitReader
import laserdisc.sbt.{DefaultsCategory, PluginContext, PublishDefaults}
import sbt.*
import sbt.Keys.*

object Publishing {
  val DefaultsKeyDesc = "Override the publishing defaults (e.g. group/org information) set in this plugin."
  val RepoNameKeyDesc = "Provide the repo name this codebase is located in.  Useful for code tracability."
}

/** Apply defaults related to publishing, specifically related to the organization and group names.
  * @param publishDefaults Implementations of this plugin library provide their organization information here
  * @param publishDefaultsKey The SBT key allowing the user to override `publishDefaults` locally
  * @param repoNameKey The SBT key providing the repo name for the current project
  */
case class Publishing(
    publishDefaults: PublishDefaults,
    publishDefaultsKey: SettingKey[PublishDefaults],
    repoNameKey: SettingKey[String]
)(
    implicit val ctx: PluginContext
) extends DefaultsCategory {

  override def projectSettings: Seq[Def.Setting[?]] = Seq()

  override def buildSettings: Seq[Def.Setting[?]] =
    Seq(
      publishDefaultsKey   := publishDefaults,
      pomIncludeRepository := (_ => false),
      organization         := publishDefaultsKey.value.groupId,
      organizationName     := publishDefaultsKey.value.orgName,
      homepage             := publishDefaultsKey.value.homepage(repoNameKey.value).some,
      scmInfo              := publishDefaultsKey.value.scmInfo(repoNameKey.value, getBranchName(gitReader.value)).some,
      licenses             := publishDefaultsKey.value.licenseCheck.validate(licenses.value, logger.value)
    )

  // thanks, sbt-git
  private def getBranchName(reader: ReadableGit): String = reader.withGit(_.branch)

}
