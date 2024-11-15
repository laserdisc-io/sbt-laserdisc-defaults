package laserdisc.sbt

/** Used by plugins to identify themselves (for log messages, errors etc)
  * @param pluginName The identity of the plugin, e.g. `sbt-foo-defaults`
  * @param pluginVersion The version of the plugin (e.g. from https://github.com/sbt/sbt-buildinfo )
  * @param pluginHomepage The URL of the homepage for this plugin
  */
case class PluginContext(
    pluginName: String,
    pluginVersion: String,
    pluginHomepage: String
)
