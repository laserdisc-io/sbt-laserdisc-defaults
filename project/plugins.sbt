//logLevel := Level.Debug
resolvers += Classpaths.sbtPluginReleases

addSbtPlugin("org.scalameta"  % "sbt-scalafmt"        % "2.5.2")
addSbtPlugin("com.github.sbt" % "sbt-native-packager" % "1.10.4")
addSbtPlugin("com.eed3si9n"   % "sbt-buildinfo"       % "0.12.0")

// this triggers "Defaulting to no-operation (NOP) logger implementation" message in projects.. ignoring warning for now
addSbtPlugin("com.github.sbt" % "sbt-git" % "2.1.0")
