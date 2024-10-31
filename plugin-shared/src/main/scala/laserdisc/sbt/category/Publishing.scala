package laserdisc.sbt.category

import com.github.sbt.git.ReadableGit
import com.github.sbt.git.SbtGit.GitKeys.gitReader
import laserdisc.sbt.{DefaultsCategory, PluginContext, PublishDefaults}
import sbt.*
import sbt.Keys.*

case class Publishing(
    publishDefaultsKey: SettingKey[PublishDefaults],
    repoNameKey: SettingKey[String],
    publishDefaults: PublishDefaults
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
  private def getBranchName(reader: ReadableGit): String =
    reader.withGit { t =>
      t.branch
    }

}
