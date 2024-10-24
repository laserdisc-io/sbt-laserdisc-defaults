package laserdisc

import _root_.sbt.*

package object sbt {

  type PublishLicense = (String, URL)

  object UsefulURLs {

    val CodeOwnersHelp = url(
      "https://docs.github.com/en/repositories/managing-your-repositorys-settings-and-features/customizing-your-repository/about-code-owners"
    )
  }

  val log: SettingKey[Logger] = Keys.sLog

  def fail[T](msg: String, e: Throwable)(implicit ctx: PluginContext): T =
    throw new MessageOnlyException(s"[${ctx.pluginName}] $msg - ${e.getClass.getSimpleName}:${e.getMessage}")

  // add some consistency to the logs
  implicit class LoggerOps(val logger: Logger) extends AnyVal {

    private[this] def fmt(msg: String)(implicit ctx: PluginContext): String = s"[${ctx.pluginName}] $msg"

    def pluginDebug(value: String)(implicit ctx: PluginContext): Unit = logger.debug(fmt(value))

    def pluginInfo(value: String)(implicit ctx: PluginContext): Unit = logger.info(fmt(value))

    def pluginWarn(value: String)(implicit ctx: PluginContext): Unit = logger.warn(fmt(value))

    def pluginError(value: String)(implicit ctx: PluginContext): Unit = logger.error(fmt(value))

  }

}
