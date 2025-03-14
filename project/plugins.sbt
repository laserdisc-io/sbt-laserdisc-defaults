//logLevel := Level.Debug
resolvers += Classpaths.sbtPluginReleases

addSbtPlugin("org.scalameta"  % "sbt-scalafmt"        % "2.5.4")
addSbtPlugin("com.github.sbt" % "sbt-native-packager" % "1.11.1")
addSbtPlugin("com.eed3si9n"   % "sbt-buildinfo"       % "0.13.1")
addSbtPlugin("com.github.sbt" % "sbt-ci-release"      % "1.9.3")

// this triggers "Defaulting to no-operation (NOP) logger implementation" message in projects.. ignoring warning for now
addSbtPlugin("com.github.sbt" % "sbt-git" % "2.1.0")
