package laserdisc.sbt.category

import com.github.sbt.git.ReadableGit
import com.github.sbt.git.SbtGit.GitKeys.gitReader
import laserdisc.sbt.{DefaultsCategory, PluginContext, PublishInfo}
import sbt.*
import sbt.Keys.*
import sbt.librarymanagement.License.MIT

case class Publishing(publishSettingKey: SettingKey[PublishInfo], repoNameKey: SettingKey[String], publishSettingsDefaults: PublishInfo)(
    implicit val ctx: PluginContext
) extends DefaultsCategory {

  private type License = (String, URL)

  override def projectSettings: Seq[Def.Setting[?]] = Seq()

  override def buildSettings: Seq[Def.Setting[?]] =
    Seq(
      publishSettingKey    := publishSettingsDefaults,
      pomIncludeRepository := (_ => false),
      organization         := publishSettingKey.value.publishGroupId,
      organizationName     := publishSettingKey.value.publishOrgName,
      homepage             := publishSettingKey.value.homepage(repoNameKey.value).some,
      scmInfo              := publishSettingKey.value.scmInfo(repoNameKey.value, getBranchName(gitReader.value)).some

      // licenses
//      licenses := validateLicense(logger.value, licenses.value)
    )

  // thanks, sbt-git
  private def getBranchName(reader: ReadableGit): String = {

    reader.withGit { t =>
      println(s"AW SHUCKS: ${t.branch}")
      t
    }

    Option(reader.withGit(_.branch)).getOrElse {
      "poopBranch"
//      throw new MessageOnlyException("Failed to determine current branch")
    }
  }

  def validateLicense(log: Logger, buildDefLicense: Seq[License]): Seq[License] =
//    val licenseFile = readFile(log, file("LICENSE"))

//    (licenseFile, buildDefLicense, pluginSettings.license) match {
//
//      case (Right(_), Seq(), None) =>
//        throw new MessageOnlyException("LICENSE file present but LICENSE not ")
//
//    }

//    pluginSettings.license match {
//      case Some(declared) => ???
//      case None =>
//        licenseFile match {
//          case Left(_) => fallback
//          case Right(contents) =>
//            if (!contents.split("\n").headOption.exists(_.contains("MIT License"))) {
//              throw new MessageOnlyException(
//                "LICENSE file doesn't look like an MIT license, so you'll need to provide the 'license' attribute"
//              )
//            }
//            License.MIT
//        }
//
//    }

    Seq(MIT)

}
