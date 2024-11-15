package laserdisc.sbt.category

import laserdisc.sbt.*
import laserdisc.sbt.io.*
import com.typesafe.sbt.SbtNativePackager.Universal
import com.typesafe.sbt.packager.Keys.dist
import sbt.*

/** Right now, this just enforces the presence of a CODEOWNERS file.
  */
case class Standards()(implicit val ctx: PluginContext) extends DefaultsCategory {

  private lazy val laserdiscCheckForCodeOwners = taskKey[Unit]("Check that the repo has a valid CODEOWNERS file")

  override def projectSettings: Seq[Def.Setting[?]] = Seq(
    laserdiscCheckForCodeOwners := {
      val target = file("CODEOWNERS")
      verifyNonEmptyFileExists(
        log = log.value,
        file = target,
        isEmptyLine = l => l.isBlank || l.trim.startsWith("#"),
        failureHelp = s"A valid CODEOWNERS file is expected - See ${UsefulURLs.CodeOwnersHelp} for more information"
      )
      log.value.pluginInfo(s"CODEOWNERS file exists and is non-empty")
    },
    Universal / dist := (Universal / dist).dependsOn(laserdiscCheckForCodeOwners).value
  )

}
