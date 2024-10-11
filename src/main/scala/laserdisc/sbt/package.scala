package laserdisc

import _root_.sbt.*
import laserdisc.sbt.defaults.PluginInfo

package object sbt {

  object UsefulURLs {
    val ThisPluginRepo: URI = uri("https://github.com/laserdisc-io/sbt-laserdisc-defaults")
    val CodeOwnersHelp = url(
      "https://docs.github.com/en/repositories/managing-your-repositorys-settings-and-features/customizing-your-repository/about-code-owners"
    )
  }

  val log: SettingKey[Logger] = Keys.sLog

  def fail[T](msg: String, e: Throwable): T = throw new MessageOnlyException(s"$prefix$msg - ${e.getClass.getSimpleName}:${e.getMessage}")

  private[this] val prefix: String = s"[${PluginInfo.name}] "

  // add some consistency to the logs
  implicit class LoggerOps(val logger: Logger) extends AnyVal {

    def pluginDebug(value: String): Unit = logger.debug(s"$prefix$value")

    def pluginInfo(value: String): Unit = logger.info(s"$prefix$value")

    def pluginWarn(value: String): Unit = logger.warn(s"$prefix$value")

    def pluginError(value: String): Unit = logger.error(s"$prefix$value")

  }

}
