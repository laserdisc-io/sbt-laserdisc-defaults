package laserdisc.sbt

import laserdisc.sbt.io.readFile
import sbt.librarymanagement.License.MIT
import sbt.{Logger, ScmInfo, URL, file, url}

/** Used to define plugin-wide defaults related to packaging and identity
  */
trait PublishDefaults {

  /** @return Organization descriptive name, e.g. `Springfield Nuclear Power Plant` */
  def orgName: String

  /** @return Package identifier, e.g. `com.springfieldnuclear` */
  def groupId: String

  /** Get the homepage associated with this plugin
    * @param repoName The repository name for the current project, which might be useful for building a repo-based URL
    */
  def homepage(repoName: String): URL

  /** @return The SCM information used when building deployable packages
    */
  def scmInfo(repoName: String, gitBranch: String): ScmInfo

  /** @return An optional [[LicenseCheck]] function to validate the LICENSE information.
    */
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

  /** Determine/update the licensing information in this project
    * @param existing The existing license definition in the SBT definition, if any
    * @return The new (or existing) license definition to set
    */
  def validate(existing: Seq[PublishLicense], logger: Logger)(implicit ctx: PluginContext): Seq[PublishLicense]
}

/** No checks - just use whatever definition they have (if any)
  */
object NotRequired extends LicenseCheck {
  override def validate(existing: Seq[PublishLicense], logger: Logger)(implicit ctx: PluginContext): Seq[PublishLicense] = existing
}

/** Check that a LICENSE file exists.  Also, best-effort check that the `licenses` definition is compatible
  * with the LICENSE file, or attempt to auto-set the definition for the LICENSE file.
  */
object LicenseRequired extends LicenseCheck {

  override def validate(userProvided: Seq[PublishLicense], logger: Logger)(implicit ctx: PluginContext): Seq[PublishLicense] =
    readFile(logger, file("LICENSE")) match {
      case Left(err) =>
        fail("A LICENSE file is required, but was not found", err)
      case Right(content) =>
        userProvided match {

          case s if s.nonEmpty =>
            logger.pluginInfo(s"LICENSE file found.  `licenses` set ($userProvided).")
            userProvided

          case Nil if appearsMIT(content) =>
            logger.pluginInfo(s"LICENSE found, appears MIT - setting license := $MIT")
            Seq(MIT)

          // laserdisc really tends to use MIT, but feel free to contribute apache 2.0 detection
          case _ =>
            fail("A LICENSE file was found, but unsure what license it is.  Please set `license`.")
        }

    }

  // short of pulling in a fuzzy-text matching library, this hack seems to do a reasonable job
  private def appearsMIT(content: String): Boolean =
    content
      .split("\n")
      .filterNot(_.trim.isEmpty)
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
