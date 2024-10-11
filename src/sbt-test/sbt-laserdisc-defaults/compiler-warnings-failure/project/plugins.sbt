sys.props.get("plugin.version") match {
  case Some(x) => addSbtPlugin("io.laserdisc" % "sbt-laserdisc-defaults" % x)
  case _       => sys.error("Plugin version needs to be set via -Dplugin.version=x.y.z")
}

libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.14"
