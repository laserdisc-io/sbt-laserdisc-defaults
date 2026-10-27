//logLevel := Level.Debug

addSbtPlugin("org.scalameta"  % "sbt-scalafmt"        % "2.6.2")
addSbtPlugin("com.github.sbt" % "sbt-native-packager" % "1.13.0")
addSbtPlugin("com.eed3si9n"   % "sbt-buildinfo"       % "0.13.2")
addSbtPlugin("com.github.sbt" % "sbt-ci-release"      % "1.12.1")

// this triggers "Defaulting to no-operation (NOP) logger implementation" message in projects.. ignoring warning for now
addSbtPlugin("com.github.sbt" % "sbt-git" % "2.2.0")
