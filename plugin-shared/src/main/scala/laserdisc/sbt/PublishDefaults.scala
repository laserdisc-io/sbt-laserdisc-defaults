package laserdisc.sbt

import laserdisc.sbt.io.readFile
import sbt.librarymanagement.License.MIT
import sbt.{Logger, ScmInfo, URL, file, url}

trait PublishDefaults {
  def orgName: String
  def groupId: String
  def homepage(repoName: String): URL
  def scmInfo(repoName: String, gitBranch: String): ScmInfo
  def licenseCheck: LicenseCheck = NotRequired
}

trait GithubPublishDefaults extends PublishDefaults {
  def githubOrg: String

  override def homepage(repoName: String): URL = url(s"https://github.com/$githubOrg/$repoName")

  override def scmInfo(repoName: String, gitBranch: String): ScmInfo = ScmInfo(
    url(s"https://github.com/$githubOrg/$repoName/tree/$gitBranch"),
    s"scm:git:git@github.com:$githubOrg/$repoName.git"
  )

}

trait LicenseCheck {
  def validate(existing: Seq[PublishLicense], logger: Logger)(implicit ctx: PluginContext): Seq[PublishLicense]
}

object NotRequired extends LicenseCheck {
  // just keep whatever they have
  override def validate(existing: Seq[PublishLicense], logger: Logger)(implicit ctx: PluginContext): Seq[PublishLicense] = existing
}

object LicenseRequired extends LicenseCheck {

  override def validate(userProvided: Seq[PublishLicense], logger: Logger)(implicit ctx: PluginContext): Seq[PublishLicense] =
    readFile(logger, file("LICENSE")) match {
      case Left(err) =>
        fail("A LICENSE file is required, but was not found", err)
      case Right(content) =>
        userProvided match {

          case s if s.nonEmpty =>
            logger.pluginWarn(s"LICENSE file found.  `licenses` set ($userProvided).") // TODO: reduce severity
            userProvided

          case Nil if appearsMIT(content) =>
            logger.pluginInfo(s"LICENSE found, appears MIT - setting license := $MIT")
            Seq(MIT)

          // laserdisc really tends to use MIT, but feel free to contribute apache 2.0 detection
          case _ =>
            fail("A LICENSE file was found, but unsure what license it is.  Please set `license`.")
        }

    }

  private def appearsMIT(content: String): Boolean =
    content
      .split("\n")
      .filterNot(_.isBlank)
      .headOption
      .map(_.trim)
      .exists(
        Seq(
          "MIT License",
          "The MIT License",
          "(The MIT License)",
          "The MIT License (MIT)"
        ).contains
      )

}
