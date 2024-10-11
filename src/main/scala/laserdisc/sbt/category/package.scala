package laserdisc.sbt

import sbt.{Logger, SettingKey}

package object category {

  def getSystemPropBoolean(sk: SettingKey[?], default: Boolean, logger: Logger): Boolean = {
    val name = sk.key.label
    sys.props.get(name).map(_.toLowerCase) match {
      case Some("true") =>
        logger.pluginWarn(s"System property $name present, setting attr to true")
        true
      case Some("false") =>
        logger.pluginWarn(s"System property $name present, setting attr to false")
        false
      case _ =>
        logger.pluginDebug(s"System property $name defaulting to $default")
        default
    }
  }

}
