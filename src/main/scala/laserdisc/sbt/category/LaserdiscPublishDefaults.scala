package laserdisc.sbt.category

import com.github.sbt.git.ReadableGit
import com.github.sbt.git.SbtGit.GitKeys.gitReader
import laserdisc.sbt.{DefaultsCategory, PublishSettings}
import laserdisc.sbt.io.readFile
import sbt.*
import sbt.Keys.*

case class LaserdiscPublishDefaults(publishSettingsKey: SettingKey[Option[PublishSettings]]) extends DefaultsCategory {

  private val NoIncludeRepo: MavenRepository => Boolean = _ => false

  override def projectSettings: Seq[Def.Setting[?]] = Seq()

  override def buildSettings: Seq[Def.Setting[?]] =
    Seq(
      publishSettingsKey := None, // default to unset, in case this gets used outside of laserdisc
//      organization := laserdiscPublishSettings.value
//        .map(_.groupID)
//        .getOrElse((organization.?).value match {
//          case Some(v) => v
//          case None =>
//        }),

//      organization := Def.settingDyn {
//        laserdiscPublishSettings.value match {
//          case Some(v) => Def.setting(v.orgName)
//          case None => Def.setting(null)
//        }
//      }.value,

      organizationName := publishSettingsKey.value
        .map(_.orgName)
        .getOrElse("HAW"), // TODO
      pomIncludeRepository := publishSettingsKey.value
        .map(_ => NoIncludeRepo)
        .getOrElse(pomIncludeRepository.value),

      // uhuh
      homepage := publishSettingsKey.value
        .map(s => url(s"https://github.com/laserdisc-io/${s.repoName}"))
        .orElse(homepage.value),

      // scminfo
      scmInfo := publishSettingsKey.value
        .map(s =>
          ScmInfo(
            url(s"https://github.com/laserdisc-io/${s.repoName}/tree/${getBranchName(gitReader.value)}"),
            s"scm:git:git@github.com:laserdisc-io/${s.repoName}.git"
          )
        )
        .orElse(scmInfo.value),

      // licenses
      licenses := (publishSettingsKey.value match {
        case None           => licenses.value
        case Some(settings) => Seq(settings.license.getOrElse(confirmMITLicense(logger.value)))
      })
    )

  // thanks, sbt-git
  private def getBranchName(reader: ReadableGit): String =
    Option(reader.withGit(_.branch)).getOrElse {
      throw new MessageOnlyException("Failed to determine current branch")
    }

  private def confirmMITLicense(logger: Logger): (String, URL) =
    readFile(logger, file("LICENSE")) match {
      case Left(err) => throw new MessageOnlyException("All laserdisc projects must have a LICENSE file: " + err)
      case Right(contents) =>
        if (!contents.split("\n").headOption.exists(_.contains("MIT License"))) {
          throw new MessageOnlyException(
            "LICENSE file doesn't look like an MIT license, so you'll need to provide the 'license' attribute"
          )
        }
        License.MIT
    }
}
