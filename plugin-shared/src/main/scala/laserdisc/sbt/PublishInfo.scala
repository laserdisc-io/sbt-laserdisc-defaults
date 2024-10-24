package laserdisc.sbt

import sbt.{ScmInfo, URL, url}

// just in case someone wants to use this plugin that doesn't use github
trait PublishInfo {

  def publishOrgName: String

  def publishGroupId: String

  def homepage(repoName: String): URL

  def scmInfo(repoName: String, gitBranch: String): ScmInfo

  def license: Option[PublishLicense] = None
}

case class GithubPublishSettings(publishOrgName: String, publishGroupId: String, githubOrg: String) extends PublishInfo {

  override def homepage(repoName: String): URL = url(s"https://github.com/$githubOrg/$repoName")

  override def scmInfo(repoName: String, gitBranch: String): ScmInfo = ScmInfo(
    url(s"https://github.com/$githubOrg/$repoName/tree/$gitBranch"),
    s"scm:git:git@github.com:$githubOrg/$repoName.git"
  )

}
