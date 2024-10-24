package laserdisc.sbt.category

import laserdisc.sbt.*
import laserdisc.sbt.category.SbtVersion.StringOps
import laserdisc.sbt.io.{FileTemplater, readResourceStream}
import org.apache.maven.artifact.versioning.ComparableVersion
import sbt.*

import java.io.ByteArrayInputStream
import java.util.Properties
import scala.collection.JavaConverters.*
import scala.tools.nsc.interpreter.InputStream
import scala.util.{Failure, Success, Try}

object SbtVersion {

  implicit class StringOps(val str: String) extends AnyVal {
    def newerThan(other: String): Boolean = new ComparableVersion(str).compareTo(new ComparableVersion(other)) > 0
  }
}

case class SbtVersion(enabledKey: SettingKey[Boolean])(implicit val ctx: PluginContext) extends FileTemplater with DefaultsCategory {

  val BuildPropsResource = "templates/project/build.properties"
  val BuildPropsDest     = new File("project/build.properties")

  override def buildSettings: Seq[Def.Setting[?]] = Seq(
    enabledKey := {
      var defaultValue = getSystemPropBoolean(enabledKey, default = true, log.value)

      // we pull the SBT version that the plugin was built with (hence going to resources)
      // then grab the SBT version from the local project (we _could_ use `sbt.Keys.sbtVersion` but this avoids testing complications)
      val pluginVersion = readSbtVersion(readResourceStream(logger.value, BuildPropsResource))

      if (BuildPropsDest.exists()) {

        val localVersion = readSbtVersion(new ByteArrayInputStream(IO.readBytes(new File("project/build.properties"))))

        if (pluginVersion.newerThan(localVersion)) {
          log.value.pluginWarn(s"build.properties sbt.version ($localVersion) is old, updating to $pluginVersion.")
        }

        if (localVersion.newerThan(pluginVersion)) {
          log.value.pluginInfo(
            s"Your sbt version ($localVersion) is newer than what ${ctx.pluginName} has ($pluginVersion), leaving " +
              s"existing version intact.  Consider publishing a new ${ctx.pluginName} release with the latest sbt " +
              s"version so everyone else can benefit!"
          )
          defaultValue = false
        }
      }

      defaultValue
    },
    generateSettings
  )

  override def enabledFlag: SettingKey[Boolean] = enabledKey

  override def inputResourceName: String = "templates/project/build.properties"

  override def outputFile = file("project/build.properties")

  def readSbtVersion(in: InputStream): String = {
    val props = Try {
      val p = new Properties()
      p.load(in)
      p
    } match {
      case Success(props) => props.asScala.toMap
      case Failure(e)     => fail(s"Failed to load inputstream as a java properties object", e)
    }

    props.getOrElse("sbt.version", throw new MessageOnlyException("failed to load sbt version from plugin resources"))

  }

}
